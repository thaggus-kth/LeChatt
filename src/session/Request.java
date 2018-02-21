package session;

import java.awt.event.ActionEvent;
import javax.swing.Timer;
import javax.swing.text.Element;

//import java.awt.event.ActionListener;

public abstract class Request implements java.awt.event.ActionListener {
	
	public static final int DEFAULT_LIFETIME = 60*1000;
	private static int nextID = 1;
	private int myID;
	private Timer timeOutTimer;
	/**
	 * Lifetime of the event in milliseconds.
	 */
	//For visibility reasons the following must be located in the subclasses.
	protected User myUser;
	private String myMessage;
	/**
	 * HTML Element in the chat log. Some requests want to modify this
	 * according to what happens with them. If so they are given an element
	 * by the SessionController as it recieves a newRequest call.
	 */
	//private Element myElement;
	
	
	public Request(User user, String message) {
		myMessage = message;
		myUser = user;
		myID = nextID++;
		timeOutTimer = new Timer(DEFAULT_LIFETIME, this);
		timeOutTimer.start();
	}
	/**
	 * Gets the instance-unique ID number of the request.
	 * @return the id number of the instance
	 */
	public int getID() {
		return myID;
	}
	
	/**
	 * Returns the username of the user sending the request
	 * @return username of myUser
	 */
	public String getUsername() {
		return myUser.username;
	}
	
	public String getMessage() {
		return myMessage;
	}
	
	/**
	 * Performs neccessary steps to accept the request.
	 * @param message User's response message to remote party.
	 */
	public abstract void accept(String message);
	
	/**
	 * Performs neccessary steps to deny the request.
	 * @param message User's response message to remote party.
	 */
	public abstract void deny(String message);
	
	/**
	 * This method is called by the internal timer when the request
	 * exceeds its lifetime. Implementations of the method should
	 * make a call to deny() along with disabling the accept() method. 
	 */
	protected abstract void timeOut();
	//TODO: we should probably make a timeOutListener interface so the popup
	//windows can display an error message if the request times out.
	
	public void actionPerformed(ActionEvent e) {
		timeOut();
	}
}
