package session;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.*;
import javax.xml.stream.XMLStreamException;

import file.Progressor;
import crypto.Crypto;
import crypto.CryptoType;
// Remove the next line once all methods are implemented
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Class which controls information flow in the chat model.
 * Create this class to initiate a chat session as client.
 * @author thaggus & ebbace
 *
 */
public class SessionController implements ConnectionObserver {
	
	protected List<User> connectedUsers = new ArrayList<User>();
	protected List<User> temporaryConnections = new ArrayList<User>();
	private List<ChatObserver> observers = new ArrayList<ChatObserver>();
	private String myUsername;
	private Color myColor;
	private HTMLDocument chatLog;
	
	/**
	 * Constructor for client controller.
	 * Creates a User representing a remote server and attempts to connect to
	 * it.
	 * @param myUsername
	 * @param myColor
	 * @param ipToConnectTo
	 * @param port
	 * @param connectionGreeting
	 * @throws IOException if there was a problem opening the connection to
	 * server.
	 * @throws XMLStreamException if there was a problem starting the XML
	 * stream.
	 */
	public SessionController(String myUsername, Color myColor,
			String ipToConnectTo, int port, String connectionGreeting)
					throws IOException, XMLStreamException {
		this(myUsername, myColor);
		User serverUser = new User(ipToConnectTo, port, this.myUsername);
		connectedUsers.add(serverUser);
		serverUser.addObserver(this);
		serverUser.sendConnectRequest(connectionGreeting);
	}
	
	/**
	 * Constructor for non-client controller.
	 * Sets up the SessionController for interacting with view but does not
	 * create any Users.
	 * @param myUsername
	 * @param myColor
	 */
	protected SessionController(String myUsername, Color myColor) {
		this.myUsername = myUsername;
		this.myColor = myColor;
				
		HTMLEditorKit kit = new HTMLEditorKit();
		chatLog = (HTMLDocument) kit.createDefaultDocument();
		/* FEATURE TESTS
		 * TODO: remove when done 
		 */
		writeToChatLog("<p>Welcome to LeChatt!</p>");
		//writeToChatLog("<a href=\"request:0\">Request test!</a>");
		//writeToChatLog("<a href=\"http://www.wikipedia.org\">Wikipedia</a>");
	}
	
	/**
	 * Takes a Message object, extracts the XML formatted 
	 * message and adds it to the chatlog. 
	 */
	@Override
	public void newMessage(Message msg) {
		String sender = msg.getSendersName();
		String htmlEscapedMessage = escapeHTML(msg.getMessage());
		String openTag = "<p style=\"color:" + colorToHex(msg.getColor())
							+ "\">";
		if (msg.getCryptoType() != CryptoType.PLAIN) {
			sender += "<i>(Krypterat med " + msg.getCryptoType().toString()
					+ ")</i>";
		}
		sender += ":";
		writeToChatLog(openTag + sender + htmlEscapedMessage + "</p>");
	}
	
	/**
	 * Adds a request link to the chatlog.The link ID is on the form
	 * user_sending_the_request:request_ID 
	 */
	@Override
	public void newRequest(Request rIn) {
		//TODO (long term): make this more informative
		// maybe the Requests could write their own element texts
		// if the sessioncontroller passes them an Element?
		String message = "You have a new request! ";
		String attribute = "\"request:" + rIn.getID() + "\"";
		String openTag = "<a href=" + attribute + 
				"title=\"Get information about request\"" + ">";
		String link = openTag + "Click here to accept or deny!" + "</a>";
		writeToChatLog(message + link);
	}
	
	@Override
	public void newNotification(User u, String s) {
		writeToChatLog(s);
		notifyObservers();
	}		
//	
//	public void newRequestReply(Message msg) {
//		String reply = msg.getMessage();
//		writeToChatLog(reply);
//		// TODO: Implement
//		throw new NotImplementedException();
//	}
	
	public Request getRequest(int requestID) {
		Request wantedRequest = null;
		for (User u : connectedUsers)
			for(Request r : u.myRequests) {
				if (r.getID() == requestID) {
					wantedRequest = r;
				}/* else {
					//TODO: implement an exception type for this
					 * Move this to outside for loops
					System.err.println("SessionController: the requested "
							+ "request ID is not in use.");
				}*/
		}
		if (wantedRequest == null) {
			for (User u : temporaryConnections) {
				for(Request r : u.myRequests) {
					if (r.getID() == requestID) {
						wantedRequest = r;
					}
				}
					
			}
		}
		/* THE NEXT STATEMENT IS FOR TEST PURPOSES
		 * TODO: REMOVE WHEN DONE
		 */
		if (wantedRequest == null) {
			wantedRequest = new Request(null, "test") {

				@Override
				public void accept(String message) {
					System.out.println("accepted test request");
				}

				@Override
				public void deny(String message) {
					System.out.println("denied test request");
				}

				@Override
				protected void timeOut() {}
				
				@Override
				protected void kill() {}
				
			};
		}
		return wantedRequest;
	}
	
	public User getUserByID(int userID) {
		User wantedUser = null;
		for (User u : connectedUsers) {
			if (u.getID() == userID)
				wantedUser = u;
		}
		if (wantedUser == null) {
			throw new IllegalArgumentException(
					"No user with that ID is connected!");
		}
		return wantedUser;
	}
	
	/** 
	 * Checks if the crypto is available for the user, and if so creates a new
	 * OutgoingFileRequest object. If not, notifies that the crypto is
	 * unavailable for the specified user.
	 * @param userID ID of User receiving the request
	 * @param file File to be sent
	 * @param c CryptoType used
	 * @param message Accompanying message to the request
	 * @return a handle to the underlying progressor. Interested observers
	 * can add themselves to this Progressor to get progress updates.
	 */
	public Progressor sendFileRequest(int userID, File file, CryptoType c, 
								String message) {
		Progressor p = null;
		if (checkCryptoAvailable(userID, c) 
				&& userAvailableForFileRequest(userID)) {
			p = getUserByID(userID).sendFileRequest(file, c, message);
		} else {
			//TODO: throw an exception
			System.err.println("Tried to send file request with unavailable"
					+ " crypto or to a busy user.");
		}
		return p;
	}
	
	/**
	 * Checks if a user is available for sending a file request.
	 * A user may only have one file request awaiting a response at any time.
	 * Once a transfer has been started, however, new requests may be sent.
	 * @param userID the user to check.
	 * @return true if it is currently possible to send a file request.
	 * false if not.
	 */
	public boolean userAvailableForFileRequest(int userID) {
		boolean answer = true;
		for (Request r : getUserByID(userID).myRequests) {
			if (r instanceof OutgoingFileRequest) {
				answer = !((OutgoingFileRequest) r).isAwaitingReply();
			}
		}
		return answer;
	}
	
	
	/**
	 * Creates a Outgoing keyrequest object.
	 * @param userID ID of User to receive the request
	 * @param c	Crypto for which a key is requested
	 * @param message Text message accompanying the request
	 */
	public void sendKeyRequest(int userID, CryptoType c, String message) {
		User receiver = getUserByID(userID);
		receiver.sendKeyRequest(c, message);
	}
	
	
	/**
	 * Checks whether the specified user can encrypt/decrypt the specified crypto
	 * @param user checked
	 * @param c	Crypto type investigated
	 * @return true if crypto is available for the user
	 */
	public boolean checkCryptoAvailable(String user, CryptoType c) {
		boolean answer = false;
		User u = stringToUser(user);
		if (c == CryptoType.PLAIN) {
			answer = true;
		} else {
			answer = u.myCryptos.containsKey(c); 
		}
		return answer;
	}
	
	public boolean checkCryptoAvailable(int userID, CryptoType c) {
		boolean answer = false;
		if (c == CryptoType.PLAIN) {
			answer = true;
		} else {
			answer = getUserByID(userID).myCryptos.containsKey(c);
		}
		return answer;
	}
	
	public CryptoType[] getAvailableCryptos(int userID) {
		User u = getUserByID(userID);
		return u.myCryptos.keySet().toArray(new CryptoType[0]);
	}
	
	/**
	 * Sets active crypto for specified user
	 * @param user whose crypto is changed
	 * @param c cryptoType changed to
	 */
	public void setCrypto(String user, CryptoType c) {
		User receiver = stringToUser(user);
		receiver.setActiveCrypto(c);
	}
	
	public void setCrypto(int userID, CryptoType c) {
		User u = getUserByID(userID);
		u.setActiveCrypto(c);
	}
	
	public void sendTextMessage(String message) {
		String openTag = "<p style=\"color:" + colorToHex(myColor) + "\">";
		String insert = openTag + myUsername + " <i>(du)</i>: "
						+ escapeHTML(message) + "</p>";
		writeToChatLog(insert);
		Message textMessage = new Message(myUsername, message, myColor);
		for(User connectedUser : connectedUsers) {
			connectedUser.sendMessage(textMessage);
		}
	}
	
	/**
	 * sets color
	 * @param c color
	 */
	public void setMyColor(Color c) {
		myColor = c;
	}
	
	/**
	 * Sets username
	 * @param u username
	 */
	public void setMyUsername(String u) {
		myUsername = u;
	}
	
	/**
	 * For server, returns the usernames of connected clients. For client, 
	 * returns the username of the server. 
	 * @return ArrayList of strings of usernames. 
	 */
	public ArrayList<String> getUsernameList() {
		ArrayList<String> UsernameList = new ArrayList<String>();
		for( User u : connectedUsers) {
			UsernameList.add(u.getUsername());
		}
		return UsernameList;
	}
	
	/**
	 * Returns an id-username map of all connected users.
	 * @return a Map of the connected users.
	 */
	public TreeMap<Integer, String> getUsernamesAndIDs() {
		TreeMap<Integer, String> m = new TreeMap<Integer, String>();
		for (User u : connectedUsers) {
			m.put(u.getID(), u.getUsername());
		}
		return m;
	}
	
	/**
	 * Gets the HTMLDocument containing the XML parsed chatlog
	 * @return HTMLDocument containing the chatLog in XML format.
	 */
	public HTMLDocument getChatLog() {
		return chatLog;
	}
	
	/**
	 * Adds HTMl formatted text to the chat log. Notifies observers that the log
	 * has been changed. 
	 * @param message HTML formatted text containing the text message
	 * @throws BadLocationException
	 * @throws IOException
	 */
	void writeToChatLog(String htmlFormattedMessage) {
		try {
			chatLog.insertBeforeEnd(chatLog.getDefaultRootElement(),
					htmlFormattedMessage);
		} catch (BadLocationException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.print(e);
		}
		notifyObservers();
	}
	
	/**
	 * Disconnects the user specified by the username. Notifies the observers.
	 * @param username Username of the User to be kicked. 
	 */
	public void kickUser(String username) {
		User kickMe = stringToUser(username);
		kickMe.disconnect();
		connectedUsers.remove(kickMe);
		notifyObservers();
	}
	
	/**
	 * Disconnects the user specified by the user ID. Notifies the observers.
	 * @param userID the ID number of the user to kick.
	 */
	public void kickUser(int userID) {
		User kickMe = getUserByID(userID);
		kickMe.disconnect();
		connectedUsers.remove(kickMe);
		notifyObservers();
	}
	
	public void disconnect() {
		/* We must use Iterator to avoid ConcurrentModificationException */
		Iterator<User> iterator = connectedUsers.iterator();
		while (iterator.hasNext()) {
			User u = iterator.next();
			iterator.remove();
			u.disconnect();
		}
		iterator = temporaryConnections.iterator();
		while (iterator.hasNext()) {
			User u = iterator.next();
			iterator.remove();
			u.disconnect();
		}
	}
	
	
	/**
	 * Notifies observers (typically view objects) that there is an update
	 * which should be displayed to the user.
	 */
	public void notifyObservers() {
		for (ChatObserver o : observers) {
			o.updateView();
		}
	}
	

	/**
	 * Finds the User object corresponding to the String username
	 * Note that if there are multiple users with the same username only the
	 * one which connected last will be returned!
	 * @param strUsername String containing the username of the sought User
	 * @return User object corresponding to the username.
	 */
	private User stringToUser(String strUsername) {
		User wantedUser = null;
		for(User u : connectedUsers) {
			if (u.getUsername() == strUsername) {
				wantedUser = u;
			}
		}
		return wantedUser;
	}
		
		
	/**
	 * Adds a chatObserver to the sessionController
	 * @param o ChatObserver being added
	 */
	public void addObserver(ChatObserver o) {
		observers.add(o);
	}
	
	/**
	 * Gets username of local user.
	 * @return username of local user.
	 */
	public String getMyUsername() {
		return myUsername;
	}
	
	/**
	 * Based on https://stackoverflow.com/questions/3607858/convert-a-rgb-color-value-to-a-hexadecimal
	 * @param c the Color to convert
	 * @return String containing the hex representation of the color
	 */
	public static String colorToHex(Color c) {
		String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(),
									c.getBlue());
		return hex;

	}
	
	/**
	 * Not original work! Credit Bruno Eberhard.
	 * From https://stackoverflow.com/questions/1265282/recommended-method-for-escaping-html-in-java.
	 * @param String in which to escape characters
	 * @return Escaped string which is ready for input into HTML document
	 */
	public static String escapeHTML(String s) {
	    StringBuilder out = new StringBuilder(Math.max(16, s.length()));
	    for (int i = 0; i < s.length(); i++) {
	        char c = s.charAt(i);
	        if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
	            out.append("&#");
	            out.append((int) c);
	            out.append(';');
	        } else {
	            out.append(c);
	        }
	    }
	    return out.toString();
	}

	@Override
	public void userDisconnected(User source) {
		connectedUsers.remove(source);
		temporaryConnections.remove(source);
		notifyObservers();
	}
}
