package session;

import java.awt.Color;
import crypto.CryptoType;

public class Message {
	private User mySource;
	private String myMessage;
	private String mySender;
	private Color myColor;
	private CryptoType myCryptoType;
	
	/**
	 * Creates a message which is/was not encrypted..
	 * @param source
	 * @param sendername
	 * @param message
	 * @param color
	 */
	public Message(User source, String sendername, String message,
			Color color) {
		myMessage = message;
		mySource = source;
		mySender = sendername;
		myColor = color;
		/**
		 * Tracks crypto type for user information.
		 */
		myCryptoType = CryptoType.PLAIN;
	}
	
	/**
	 * Constructor for creating representations of messages sent by
	 * the local user (which are suitable for passing to User.sendMessage().
	 * @param ourUsername - our username
	 * @param ourMessage - the message we want to send
	 * @param ourColor - our color
	 */
	public Message(String ourUsername, String ourMessage, Color ourColor) {
		mySender = ourUsername;
		myMessage = ourMessage;
		myColor = ourColor;
		mySource = null;
	}
	
	/**
	 * Creates a message which was encrypted when it was recieved. Note that
	 * encrypted messages cannot be sent by calling this constructor. Instead,
	 * encrypted messages are sent by setting activeCrypto on User.
	 * @see {@link User}
	 * @param source
	 * @param sendername
	 * @param message
	 * @param color
	 * @param ct
	 */
	public Message(User source, String sendername, String message,
			Color color, CryptoType ct) {
		myMessage = message;
		mySource = source;
		mySender = sendername;
		myColor = color;
		myCryptoType = ct;
	}
	
	public User getSource() {
		return mySource;
	}
	
	public String getMessage() {
		return myMessage;
	}
	
	public String getSendersName() {
		return mySender;
	}
	
	public Color getColor() {
		return myColor;
	}
	
	public CryptoType getCryptoType() {
		return myCryptoType;
	}
}
