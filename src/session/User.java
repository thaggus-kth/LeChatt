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
	protected boolean connected = false;
	//TODO: make a set method
	protected boolean keepTemporaryConnection = true;
	ArrayList<Request> myRequests = new ArrayList<Request>();
	ArrayList<Crypto> myCryptos = new ArrayList<Crypto>();
	/** The username of the remote user or remote server. Note that attempts
	 * to change username by the remote party are ignored. */
	private String username; //TODO: this field should be made private, and a getUsername method should be added.
	/** The username of the local user.  */
	private String defaultSender;
	private Crypto activeCrypto;
	private ArrayList<ConnectionObserver> observers = new ArrayList<ConnectionObserver>();
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
		defaultSender = myServer.getMyUsername();
		connection = mySocket;
		try {
			xmlOut = myXMLWriterFactory.createXMLStreamWriter(
            		connection.getOutputStream(), "UTF-8");
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
		} catch (IOException | XMLStreamException e) {
			e.printStackTrace();
		}
		myRequests.add(myServer.new IncomingConnectionRequest(this));
		th.start();
	}
	
	/**
	 * This constructor is used by the client SessionController to create
	 * a user representing a remote server.
	 * To connect, call sendConnectRequest after construction.
	 * @param hostAddress the IP address to connect to.
	 * @param port the port to connect to.
	 * @param ourUsername the username of the local user (which will be
	 * displayed to the remote users)
	 */
	public User(String hostAddress, int port, String ourUsername) {
		Thread th = new Thread(this);
		defaultSender = ourUsername;
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
	 * Sends a Message object to the remote user.
	 * @param toSend the message to send.
	 */
	public void sendMessage(Message toSend) {
		try {
			xmlOutputMessageHeader(toSend.getSendersName());
			xmlOut.writeStartElement("text");
			xmlOut.writeAttribute("color", SessionController.
					colorToHex(toSend.getColor()));
			if (activeCrypto != null) {
				xmlOut.writeStartElement("encrypted");
				//TODO: Implement encryption
			} else {
				xmlOut.writeCharacters(toSend.getMessage());
			}
			xmlOut.writeEndDocument();
			
		} catch (XMLStreamException e) {
			if (e.getCause() instanceof IOException) {
				lostConnection();
			} else {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * sends XML formatted message over the internet. Note that this
	 * should only be used by Requests or other actors which need to
	 * write special xml tags: text messages and disconnect
	 * messages are handled via sendMessage() and disconnect().
	 * @param xmlFormattedMessage The xml element to be transmitted to
	 * remote user, ex. <keyrequest type="xxx">message here</keyrequest>
	 * 
	 */
	public void writeLine(String xmlFormattedMessage) {
		try {
			xmlOutputMessageHeader(defaultSender);
			StringReader sr = new StringReader(xmlFormattedMessage);
			XMLStreamReader xmlReader = myXMLReaderFactory.
					createXMLStreamReader(sr);
			while (xmlReader.hasNext()) {
				switch (xmlReader.next()) {
				case XMLStreamConstants.START_ELEMENT:
					xmlOut.writeStartElement(xmlReader.getLocalName());
					for (int i = 0; i < xmlReader.getAttributeCount(); i++) {
						xmlOut.writeAttribute(
								xmlReader.getAttributeLocalName(i),
								xmlReader.getAttributeValue(i));
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					xmlOut.writeCharacters(xmlReader.getText());
					break;
				case XMLStreamConstants.END_ELEMENT:
					xmlOut.writeEndElement();
					break;
				}
			}
			xmlOut.writeEndDocument();
		} catch (XMLStreamException e) {
			if (e.getCause() instanceof IOException) {
				lostConnection();
			} else {
				/* Ending up here probably means the inputted XML was not well
				 * formed. If we were writing more professional code we should
				 * probably throw an exception at this point, like
				 * IllegalArgumentException.
				 */
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method writes the opening tags of any message.
	 * TODO: maybe subclass the XMLStreamWriter and make this a class method?
	 * @throws XMLStreamException
	 */
	private void xmlOutputMessageHeader(String sender) throws XMLStreamException {
		xmlOut.writeStartDocument("UTF-8", "1.0");
		xmlOut.writeStartElement("message");
		xmlOut.writeAttribute("sender", sender);
	}
	
	/**
	 * Closes the connection to the user. Sends a disconnect message if
	 * neccessary.
	 */
	public void disconnect() {
		if (connected) {
			try {
				xmlOutputMessageHeader(defaultSender);
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
		connected = false;
		//TODO: kill all requests in myRequest.
	}
	
	public void run() {
		/* Parse the temporary connection messages. */
		try {
			while (keepTemporaryConnection) {
				XMLStreamReader xmlReader = myXMLReaderFactory.
						createXMLStreamReader(in);
				boolean msgDone = false;
				while (!msgDone && keepTemporaryConnection) {
					switch (xmlReader.next()) {
					case XMLStreamConstants.START_ELEMENT:
						switch (xmlReader.getLocalName()) {
						case "message":
							if (username == null || username.isEmpty()) {
								/* Since we don't know the username of this user,
								 * we want to record it.
								 */
								username = xmlReader.
										getAttributeValue(null, "sender");
							}
							break;
						case "request":
							/* ConnectionRequest tag. */
							if (xmlReader.getAttributeCount() > 0) {
								/* There is a reply attribute, so
								 * this is a reply.
								 */
								String reply = xmlReader.getAttributeValue(
										null, "reply");
								Request wanted = null;
								for (Request r : myRequests) {
									if (r instanceof 
											OutgoingConnectionRequest) {
										//TODO: this may need to be revised once
										// OutgoingConnectionRequest is implemented.
										wanted = r;
									}
								}
								if (wanted != null) {
									switch (reply.toLowerCase()) {
									case "yes":
										wanted.accept(null);
										break;
									case "no":
										wanted.deny(null);
										break;
									}
									//break;
								}
							} else {
								/* This is an incoming connection request. */
								for (Request r : myRequests) {
									if (r instanceof Server.
											IncomingConnectionRequest) {
										((Server.IncomingConnectionRequest) r).
										setMessage(xmlReader.getElementText());
										fireNewRequestEvent(r);
										break;
									}
								}
							}
							break;
						case "disconnect":
							keepTemporaryConnection = false;
							String msg = "User ";
							if (username != null && !username.isEmpty()) {
								msg += username + '@';
							}
							msg +=  connection.getInetAddress() +
									" disconnected.";
							fireUserNotificationEvent(msg);
							disconnect();
							break;
						case "text":
							boolean primitiveUser = true;
							for (Request r : myRequests) {
								if (r instanceof Server.
										IncomingConnectionRequest) {
									/* The other user is just being eager and
									 * writes things before we accepted their connection.
									 * TODO: save these messages in a buffer and output them
									 * when the connection has been accepted.
									 */
									primitiveUser = false;
								} else if (r instanceof
										OutgoingConnectionRequest) {
									/* We might be dealing with a more primitive user.
									 * ... or the other party might be eager. But assume
									 * the worst. */
									//TODO: notify the GUI that we might be connecting
									// to a primitive server and ask if its ok.
								}
							}
							if (primitiveUser) {
								//TODO: notify the GUI that the user who is trying
								//to connect is primitive and ask if we should allow it.
							}
							
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						if (xmlReader.getLocalName() == "message") {
							msgDone = true;
							if (connected == true) {
								keepTemporaryConnection = false;
							}
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
				e.printStackTrace();
				lostConnection();
			} else {
				/* We recieved XML which was not well-formed. */
				e.printStackTrace(); //remove this when next line is implemented.
				//TODO: notify the user that we recieved a broken message.
			}
		}
		if (connected) {
			/* connected is set true by accepting ConnectionRequest. */
			pumpMessages();
		}
		disconnect();
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
				return (sender != null && !messageBuilder.toString().isEmpty() && hexColor != null);
			}
			
			/**
			 * Call this to get a finalized message.
			 * @param source - should always be this User
			 * @return the finalized message
			 */
			Message toMessage(User source) {
				String message = messageBuilder.toString();
				Message newMessage;
				Color cOut = Color.decode(hexColor);
// These lines commented out until we know that they can be removed safely.
//				if (ct == null) {
// The next line should never be commented out.
					newMessage = new Message(source, sender, message, cOut);
//				}
//				else {
//					newMessage = new Message(source, sender, message, cOut,
//							ct);
//				}
				return newMessage;
			}
		}
		
		/* Begin XML parsing section. */
		while (connected) {
			try {
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
			    			mb.sender = xmlReader.getAttributeValue(null, 
			    					"sender");
			    			break;
			    		case "text":
			    			mb.hexColor = xmlReader.getAttributeValue(null,
			    					"color");
			    			mb.messageBuilder.append(xmlReader.getElementText());
			    			//TODO: getElementText() will throw an exception if there
			    			// are sub-tags to <text>, like <fetstil>, so we need to
			    			// think of a better way to do this.
			    			break;
			    		case "disconnect":
			    			fireUserNotificationEvent(username 
			    					+ " disconnected.");
			    			disconnect();
			    			break;
			    		case "keyrequest":
			    		case "filerequest":
			    			System.err.println("Got unimplemented request "
			    					+ "from " + username);
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
			} catch (XMLStreamException e) {
				if (e.getCause() instanceof IOException) {
					/* This means that the Socket stream was closed. */
					lostConnection();
				} else {
					/* We recieved a broken XML message (most likely) */
					e.printStackTrace(); //remove when the next line is realized.
					//TODO: notify user that we recieved a broken xml message.
				}
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
		try {
			xmlOutputMessageHeader(defaultSender);
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
			fireUserNotificationEvent(username + "@" +
					connection.getInetAddress() + " accepted our connection" + 
					" request. You can now chat!");
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
			fireUserNotificationEvent("Connection request to " +
								connection.getInetAddress() + " timed out.");
			getUser().myRequests.remove(this);
			disconnect();
		}
		
	}
	
	public void fireUserNotificationEvent(String message) {
		for (ConnectionObserver o : observers) {
			o.newNotification(this, message);
		}
	}
	
	/**
	 * Gets the remote user's name. Note that for newly connected users the
	 * username might not be known.
	 * @return remote user's name if known, empty string otherwise.
	 */
	public String getUsername() {
		return username != null ? username : "";
	}
	
	/**
	 * This method is for calling when IOExceptions were caught, since
	 * they mean that we lost the connection to the remote party unexpectedly.
	 */
	private void lostConnection() {
		String name;
		if (username == null || username.isEmpty()) {
			name = connection.getInetAddress().toString();
		} else {
			name = username;
		}
		fireUserNotificationEvent("Lost connection to " + name + ".");
		disconnect();
	}

}
