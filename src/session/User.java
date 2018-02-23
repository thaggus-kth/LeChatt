package session;

import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.stream.*;

import crypto.*;

public class User implements Runnable {
	private Socket connection;
	/** Tracks if have a connection which has been accepted
	 * by both parties.	 */
	private boolean connected = false;
	ArrayList<Request> myRequests;
	ArrayList<Crypto> myCryptos;
	/** The username of the remote user or server. Note that attempts
	 * to change username by the remote party are ignored. */
	String username; //TODO: this field should be made private, and a getUsername method should be added.
	private Crypto activeCrypto;
	private ArrayList<ConnectionObserver> observers;
	/* PrintWriter replaced by XMLStreamWriter, which
	 * accesses the out stream of the Socket directly. */
	//private PrintWriter out;
	private BufferedReader in;
	/** XML is output over the Socket by calls to this writer. */
	private XMLStreamWriter xmlOut;
	/* XMLStreamReader and XMLStreamWriter factories.
	 * We need these to parse XML with StAX.	 */
	private XMLInputFactory myXMLReaderFactory = 
											XMLInputFactory.newInstance();
	private XMLOutputFactory myXMLWriterFactory =
											XMLOutputFactory.newInstance();
	
	/**
	 * This constructor is used by the Server object when creating a new User.
	 * @param mySocket
	 */
	public User(Socket mySocket, Server myServer) {
		Thread th = new Thread(this);
		connection = mySocket;
		myRequests.add(myServer.new IncomingConnectionRequest(this));
		th.start();
	}
	
	/**
	 * This constructor is used by the client SessionController to create
	 * a user representing a remote server.
	 * To connect, call sendConnectRequest after construction.
	 * @param hostAddress
	 * @param port
	 */
	public User(String hostAddress, int port) {
		Thread th = new Thread(this);
		try {
            connection = new Socket(hostAddress, port);
            //out = new PrintWriter(connection.getOutputStream(), true);
            xmlOut = myXMLWriterFactory.createXMLStreamWriter(
            		connection.getOutputStream(), "UTF-8");
            in = new BufferedReader(new InputStreamReader(
                                        connection.getInputStream()));
            th.start();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.\n" + e);
        } catch (IOException | XMLStreamException e) {
        	System.err.println(e);
        }
	}
	
	/**
	 * Reads messages from the Socket's InputStream as they
	 * become available and acts according to their XML content.
	 */
	private void pumpMessages() {
		/**
		 * We create this local class to build messages more easily.
		 * @author thaggus
		 */
		final class MessageBuilder {
			String sender;
			String message;
			String hexColor;
			CryptoType ct = null;
			
			StringBuilder messageBuilder = new StringBuilder();
			
			/**
			 * Returns true if we have collected enough information to
			 * create a complete Message object.
			 */
			boolean isComplete() {
				return (sender != null && message != null && hexColor != null);
			}
			
			/**
			 * Call this to get a finalized message.
			 * @param source - should always be this User
			 * @return the finalized message
			 */
			Message toMessage(User source) {
				String message = messageBuilder.toString();
				Message newMessage;
				Color cOut = Color.black;//TODO: convert hexColor to a Color
				if (ct == null) {
					newMessage = new Message(source, sender, message, cOut);
				}
				else {
					newMessage = new Message(source, sender, message, cOut,
							ct);
				}
				return newMessage;
			}
		}
		
		/* Begin XML parsing section. */
		boolean shouldStop = false;
		try {
			while (!shouldStop) {
			    XMLStreamReader xmlReader = 
			    		myXMLReaderFactory.createXMLStreamReader(in);
			    boolean messageEnded = false;
			    MessageBuilder mb = new MessageBuilder();
			    while (!messageEnded) {
			    	/* The next statement blocks (i.e. the program waits)
			    	 * until there is a complete xml element (with a
			    	 * closing tag) to read from the in stream.
			    	 */
			    	xmlReader.next();
			    	switch (xmlReader.getEventType()) {
			    	case XMLStreamConstants.START_ELEMENT:
			    		switch (xmlReader.getLocalName()) {
			    		case "message":
			    			mb.sender = xmlReader.getAttributeValue(null, "sender");
			    			break;
			    		case "text":
			    			mb.hexColor = xmlReader.getAttributeValue(null, "color");
			    			mb.messageBuilder.append(xmlReader.getElementText());
			    			//TODO: getElementText() will throw an exception if there
			    			// are sub-tags to <text>, like <fetstil>, so we need to
			    			// think of a better way to do this.
			    			break;
			    		case "disconnect":
			    			shouldStop = true;
			    			fireUserNotificationEvent(username + " disconnected.");
			    			break;
			    		case "keyrequest":
			    		case "filerequest":
			    			System.err.println("Got unimplemented request from " + username);
			    			//TODO (long term): implement.
			    			break;
			    		}
			    		break;
			    	case XMLStreamConstants.CHARACTERS: //Characters = text between tags
			    		break;
			    	case XMLStreamConstants.END_ELEMENT:
			    		switch (xmlReader.getLocalName()) {
			    		case "message":
			    			if (mb.isComplete()) {
			    				/* If the message contained a <text> tag,
			    				 * mb.isComplete() will be true and we
			    				 * want to send the text message to
			    				 * the session controller for display.
			    				 */
				    			fireMessageEvent(mb.toMessage(this));
				    		}
			    			messageEnded = true;
			    		}
			    		break;
			    	case XMLStreamConstants.END_DOCUMENT: //adding this for robustness
			    		messageEnded = true;
			    		break;
			    	}
			    }
			}
		} catch (XMLStreamException e) {
			if (e.getCause() instanceof IOException) {
				/* This means that the Socket stream was closed. */
				//TODO: write information to user about other user disconnecting.
			}
		}
	}
	
	public void run() {
		
		/* Parse the temporary connection messages. */
		try {
			boolean keepTemporaryConnection = true;
			while (keepTemporaryConnection) {
				XMLStreamReader xmlReader = myXMLReaderFactory.createXMLStreamReader(in);
				boolean msgDone = false;
				while (!msgDone) {
					switch (xmlReader.next()) {
					case XMLStreamConstants.START_ELEMENT:
						switch (xmlReader.getLocalName()) {
						case "message":
							if (username == null || username.isEmpty()) {
								/* Since we don't know the username of this user,
								 * we want to record it.
								 */
								username = xmlReader.getAttributeValue(null, "sender");
							}
							break;
						case "request":
							/* ConnectionRequest tag. */
							if (xmlReader.getAttributeCount() > 0) {
								/* There is a reply attribute, so
								 * this is a reply.
								 */
								String reply = xmlReader.getAttributeValue(null, "reply");
								for (Request r : myRequests) {
									if (r instanceof OutgoingConnectionRequest) {
										//TODO: this may need to be revised once
										// OutgoingConnectionRequest is implemented.
										switch (reply.toLowerCase()) {
										case "yes":
											r.accept(null);
											break;
										case "no":
											r.deny(null);
											break;
										}
									}
								}
							} else {
								/* This is an incoming connection request. */
								for (Request r : myRequests) {
									if (r instanceof Server.IncomingConnectionRequest) {
										r = (Server.IncomingConnectionRequest) r;
										String message = xmlReader.getElementText();
										fireNewRequestEvent(r);
										break;
									}
								}
							}
							break;
						case "disconnect":
							keepTemporaryConnection = false;
							//TODO: fireUserNotificationEvent(); - notify the user of the disconnect
							break;
						}
					case XMLStreamConstants.END_ELEMENT:
						if (xmlReader.getLocalName() == "message") {
							msgDone = true;
							/* this behavior (recreating the XMLStreamReader
							 * for each <message>-tag) is needed to avoid the
							 * XML parser complaining about XML documents not
							 * being well-formed.
							 */
						}
						break;
					case XMLStreamConstants.END_DOCUMENT: //for robustness
						msgDone = true;
						break;
					}
				}
			}
		} catch (XMLStreamException e) {
			if (e.getCause() instanceof IOException) {
				/* This means that the Socket stream was closed. */
				//TODO: write information to user about other user disconnecting.
				//Note that we have the same case in pumpMessages, so make a small method for this.
			}
		}
		if (connected) {
			/* The connected boolean is set by the Request objects. */
			pumpMessages();
		}
		disconnect();
	}
	
	/**
	 * sends XML formatted message over the internet.
	 * @param xmlFormattedMessage
	 */
	public void writeLine(String xmlFormattedMessage) {
		try {
			xmlOutputMessageHeader();
			//TODO: parse xmlFormattedMessage and write it exactly
			//as is via xmlOut. Should we keep this method?
			xmlOut.writeEndDocument();
		} catch (XMLStreamException e) {
			e.printStackTrace();//make error msg
		}
	}
	
	/**
	 * This method writes the opening tags of any message.
	 * TODO: maybe subclass the XMLStreamWriter and make this a class method?
	 * @throws XMLStreamException
	 */
	private void xmlOutputMessageHeader() throws XMLStreamException {
		xmlOut.writeStartDocument("UTF-8", "1.0");
		xmlOut.writeStartElement("message");
		xmlOut.writeAttribute("sender", username);
	}
	
	/**
	 * Closes the connection to the user. Sends a disconnect message if
	 * neccessary.
	 */
	public void disconnect() {
		if (connected) {
			try {
				xmlOutputMessageHeader();
				xmlOut.writeEmptyElement("disconnect");
				xmlOut.writeEndDocument();
			} catch (XMLStreamException e) {
				/* at this point we don't care about
				 * errors, so just ignore them
				 */
			}
		}
		try {
			xmlOut.close();
		} catch (XMLStreamException e) {
			/* i don't care about your exceptions */
		}
		if (!connection.isClosed()) {
			try {
				connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void addObserver(ConnectionObserver o) {
		observers.add(o);
	}
	
	public void fireMessageEvent(Message m) {
		for (ConnectionObserver o : observers) {
			o.newMessage(m);
		}
	}
	
	public void fireNewRequestEvent(Request r) {
		for (ConnectionObserver o : observers) {
			o.newRequest(r);
		}
	}
	
	public void addRequest(Request r) {
		myRequests.add(r);
	}
	
//	public void sendKeyRequest(CryptoType c, String message) {
//		Request keyRequest = new OutgoingKeyRequest(Request.DEFAULT_LIFETIME, message, this, c);
//		myRequests.add(keyRequest);
//	}
	
//	public void sendFileRequest(File file, CryptoType c, String message) {
//		Request fileRequest = new OutgoingFileRequest(60, message, this, file, c);
//		myRequests.add(fileRequest);
//	}
	
	public void setActiveCrypto(CryptoType c) {
		switch (c) {
			case CAESAR:
				//activeCrypto = CaesarCrypto;
				break;
			case AES:
				//activeCrypto = AESCrypto;
				break;
		}
	}
	
	public void sendConnectRequest(String message) {
		Request connectRequest = new OutgoingConnectionRequest(message, this);
		myRequests.add(connectRequest);
		/* This request does not need to be fired. */
		try{
			xmlOutputMessageHeader();
			xmlOut.writeStartElement("request");
			xmlOut.writeCharacters(message);
			xmlOut.writeEndDocument();
		} catch (XMLStreamException e) {
			System.err.println("Couldn't send connection request to " +
								connection.getInetAddress() + ": " + e);
		}
	}
	
	/**
	 * Request subclass which handles outgoing connection requests.
	 * @author thaggus
	 */
	public class OutgoingConnectionRequest extends ConnectionRequest {
		
		public OutgoingConnectionRequest(String message, User user) {
			super(user, message);
		}
	
		@Override
		public void accept(String message) {
			connected = true;
			getMyElement();
			//TODO: modify myElement
			getUser().myRequests.remove(this);
		}
	
		@Override
		public void deny(String message) {
			/* The remote user denied our request. */
			StringBuilder msgOut = new StringBuilder();
			msgOut.append(username);
			msgOut.append('@');
			msgOut.append(connection.getInetAddress());
			msgOut.append(" denied our request to connect");
			if (message != null && !message.isEmpty()) {
				msgOut.append(" with the message: \"");
				msgOut.append(message);
				msgOut.append("\"");
			} else {
				msgOut.append(".");
			}
			fireUserNotificationEvent(msgOut.toString());
			getUser().myRequests.remove(this);
			disconnect();
		}
	
		@Override
		protected void timeOut() {
			fireUserNotificationEvent("Connection request to " + connection.getInetAddress() +
					" timed out.");
			getUser().myRequests.remove(this);
			disconnect();
		}
		
	}
	
	public void fireUserNotificationEvent(String message) {
		for (ConnectionObserver o : observers) {
			o.newNotification(this, message);
		}
	}

}
