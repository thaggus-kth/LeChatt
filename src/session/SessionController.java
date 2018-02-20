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
	
	private List<User> connectedUsers;
	private List<ChatObserver> observers;
	private String myUsername;
	private Color myColor;
	private HTMLDocument chatLog;

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
		//TODO: Create a user object & attempt connecting to the
		//		server
	}
	
	protected SessionController(String myUsername, Color myColor) {
		this.myUsername = myUsername;
		this.myColor = myColor;
	}
	
	public void newMessage(Message msg) {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	public void newRequest(Request rIn) {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	public void newNotification(Message msg) {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	public Request getRequest(String user, int id) {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	public void sendFileRequest(String user, CryptoType c) {
		// TODO: Implement
		throw new NotImplementedException();
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
	            return true
	        }
	    }
	    return false;
	}
	
	public void sendKeyRequest(String user, CryptoType c, String message) {
		Request keyRequest = new OutgoingKeyRequest(DEFAULT_LIFETIME, message, user, )
	}
	
	public void setCrypto(String user, CryptoType c) {
		user.setActiveCrypto(c);
	}
	
	public void sendTextMessage(String message) {
		// TODO: Implement
		//throw new NotImplementedException();
		String openTag = "<p style=\"color:" + colorToHex(myColor) + "\">";
		String insert = openTag + message + "</p>";
		writeToChatLog(insert);
	}
	
	/**
	 * sets user's color
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
	 * Adds HTMl formatted text to the chat log if there is one. Creates a HTML
	 * document if there is none already
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
	
	public void kickUser() {
		// TODO: Implement
		throw new NotImplementedException();
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
