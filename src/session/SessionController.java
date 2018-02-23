package session;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.*;

// Remove the next line once all methods are implemented
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Class which controls information flow in the chat model.
 * Create this class to initiate a chat session as client.
 * @author thaggus & ebbace
 *
 */
public class SessionController implements ConnectionObserver {
	
	protected List<User> connectedUsers;
	private List<ChatObserver> observers;
	private String myUsername;
	private Color myColor;
	private HTMLDocument chatLog;
	
	/**
	 * Constructor for client controller.
	 * Creates a server User
	 * @param myUsername
	 * @param myColor
	 * @param ipToConnectTo
	 * @param port
	 * @param connectionGreeting
	 */
	public SessionController(String myUsername, Color myColor,
			String ipToConnectTo, int port, String connectionGreeting) {
		this.myUsername = myUsername;
		this.myColor = myColor;
		//chatLog = new HTMLDocument();
		connectedUsers = new ArrayList<User>();
		observers = new ArrayList<ChatObserver>();
		
		HTMLEditorKit kit = new HTMLEditorKit();
		chatLog = (HTMLDocument) kit.createDefaultDocument();
		writeToChatLog("<p>Welcome to LeChatt!</p>");
		
		User serverUser = new User(ipToConnectTo, port);
		connectedUsers.add(serverUser);
		Request connectionRequest = new OutgoingConnectionRequest(serverUser, connectionGreeting);		
	}
	
	protected SessionController(String myUsername, Color myColor) {
		this.myUsername = myUsername;
		this.myColor = myColor;
	}
	
	/**
	 * Takes a Message object, extracts the XML formatted 
	 * message and adds it to the chatlog. 
	 */
	public void newMessage(Message msg) {
		String message = msg.getMessage();
		writeToChatLog(message);
	}
	
	/**
	 * Adds a request link to the chatlog.The link ID is on the form
	 * user_sending_the_request:request_ID 
	 */
	public void newRequest(Request rIn) {
		String userAndID = "\"" + rIn.getUsername() + ":" + rIn.getID() + "\"";
		String openTag = "<a href =" + userAndID + 
				"title = \"Get information about request\"" + ">";
		String insert = openTag + "New Request!" + "</a>";
		writeToChatLog(insert);
	}
//	
//	public void newRequestReply(Message msg) {
//		String reply = msg.getMessage();
//		writeToChatLog(reply);
//		// TODO: Implement
//		throw new NotImplementedException();
//	}
	
	public Request getRequest(String user, int id) {
		User u = stringToUser(user);
		Request request;
		for(Request r : u.myRequests) {
			if (r.getID == id) {
				request = r;
			}
		} else
			// Request ID not correct
	}
	
	/** 
	 * Checks if the crypto is available for the user, and if so creates a new
	 * OutgoingFileRequest object. If not, notifies that the crypto is
	 * unavailable for the specified user.
	 * @param user User receiving the request
	 * @param file File to be sent
	 * @param c CryptoType used
	 * @param message Accompanying message to the request
	 */
	public void sendFileRequest(String user, File file, CryptoType c, 
								String message) {
		if (checkCryptoAvailable(user, c)) {
			User receiver = stringToUser(user); 
			Request fileRequest = new OutgoingFileRequest(Request.DEFAULT_LIFETIME,
					message, receiver, file, c);
			newRequest(fileRequest);
		} else {
			//notify that the crypto is unavailable
		}
	}
	
	
	/**
	 * Creates a Outgoing keyrequest object and forwards it to the newRequest method
	 * @param user User to receive the request
	 * @param c	Crypto for which a key is requested
	 * @param message Text message accompanying the request
	 */
	public void sendKeyRequest(String user, CryptoType c, String message) {
		User receiver = stringToUser(user);
		Request keyRequest = new OutgoingKeyRequest(Request.DEFAULT_LIFETIME, message, receiver, c);
		newRequest(keyRequest);
		}
	}
	
	/**
	 * Checks whether the specified user can encrypt/decrypt the specified crypto
	 * @param user checked
	 * @param c	Crypto type investigated
	 * @return true if crypto is available for the user
	 */
	public boolean checkCryptoAvailable(String user, CryptoType c) {
		for(Crypto crypto : user.myCryptos) {
	        if(crypto.getType() == c) {
	            return true;
	        }
	    }
	    return false;
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
	
	public void sendTextMessage(String message) {
		String openTag = "<p style=\"color:" + colorToHex(myColor) + "\">";
		String insert = openTag + message + "</p>";
		writeToChatLog(insert);
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
			UsernameList.add(u.username);
		}
		return UsernameList;
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
	void writeToChatLog(String message) {
		try {
			chatLog.insertBeforeEnd(chatLog.getDefaultRootElement(), message);
		} catch (BadLocationException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.print(e);
		}
		System.out.print(message);
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
	
	public void disconnect() {
		// TODO: Implement
		//throw new NotImplementedException();
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
	 * @param strUsername String containing the username of the sought User
	 * @return User object corresponding to the username.
	 */
	private User stringToUser(String strUsername) {
		User wantedUser;
		for(User u : connectedUsers) {
			if (u.username == strUsername) {
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
	 * Based on https://stackoverflow.com/questions/3607858/convert-a-rgb-color-value-to-a-hexadecimal
	 * @param c the Color to convert
	 * @return String containing the hex representation of the color
	 */
	public static String colorToHex(Color c) {
		String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(),
									c.getBlue());
		return hex;

	}
}
