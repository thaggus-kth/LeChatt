package session;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.html.HTMLDocument;
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
			String ipToConnectTo, int port) {
		this.myUsername = myUsername;
		this.myColor = myColor;
		chatLog = new HTMLDocument();
		connectedUsers = new ArrayList<User>();
		observers = new ArrayList<ChatObserver>();
		
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
	
	public void checkCryptoAvailable(String user, CryptoType c) {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	public void sendKeyRequest(String user, CryptoType c, String message) {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	public void setCrypto(String user, CryptoType c) {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	public void sendTextMessage(String message) {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	public void setMyColor(Color c) {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	public void setMyUsername(String u) {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	public String[] getUsernameList() {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	void writeToChatLog(String message) {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	public void kickUser() {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	public void disconnect() {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	/* Renamed from UML: getHTMLDocument to getChatLog */
	public HTMLDocument getChatLog() {
		return chatLog;
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
}
