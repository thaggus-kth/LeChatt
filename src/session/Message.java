package session;

import java.awt.Color;
import crypto.CryptoType;

public class Message {
	private User mySource;
	private String myMessage;
	private String mySender;
	private Color myColor;
//	private CryptoType myCryptoType;
	
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
		/* Jag blev osäker på om Message ska ha koll på sitt crypto eller
		 * inte... antagligen inte eftersom vi ska skicka ett krypterat
		 * meddelande till alla användare oavsett om de har kryptering
		 * aktiverat eller ej. Men låter denna egenskap vara kvar så länge.
		 */
//		myCryptoType = CryptoType.PLAIN;
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
	
//	/**
//	 * Creates a message which should be encrypted before being sent over
//	 * the internet. Note that encryption only occurs if the message is
//	 * actually transmitted over the net and is done by the User class.
//	 * @param source
//	 * @param sendername
//	 * @param message
//	 * @param color
//	 * @param ct
//	 */
//	public Message(User source, String sendername, String message,
//			Color color, CryptoType ct) {
//		myMessage = message;
//		mySource = source;
//		mySender = sendername;
//		myColor = color;
//		myCryptoType = ct;
//	}
	
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
	
//	public CryptoType getEncryptionType() {
//		return myCryptoType;
//	}
}
