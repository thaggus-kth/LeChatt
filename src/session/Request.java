package session;

import javax.swing.text.Element;

//import java.awt.event.ActionListener;

public abstract class Request implements java.awt.event.ActionListener {
	
	public static final int DEFAULT_LIFETIME_SECONDS = 60;
	private static int nextID = 1;
	private int myID;
	/**
	 * Lifetime of the event in miliseconds.
	 */
	private int lifetime = DEFAULT_LIFETIME_SECONDS * 1000;
	private User myUser;
	private String myMessage;
	/* HTML Element in the chat log. Some requests want to modify this
	 * according to what happens with them. If so they are given an element
	 * by the SessionController as it recieves a newRequest call.
	 */
	private Element myElement;
	
	//TODO: add remaining methods.
}
