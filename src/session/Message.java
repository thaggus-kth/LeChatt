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
	 * Creates an unencrypted message.
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
		myCryptoType = CryptoType.PLAIN;
	}
	
	/**
	 * Creates a message which should be encrypted before being sent over
	 * the internet. Note that encryption only occurs if the message is
	 * actually transmitted over the net and is done by the User class.
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
	
	public CryptoType getEncryptionType() {
		return myCryptoType;
	}

}
