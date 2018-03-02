package session;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Timer;
import javax.swing.text.Element;

//import java.awt.event.ActionListener;

public abstract class Request implements java.awt.event.ActionListener {
	
	public static final int DEFAULT_LIFETIME = 60*1000;
	private static int nextID = 1;
	private List<RequestObserver> observers = new ArrayList<RequestObserver>();
	private int myID;
	private Timer timeOutTimer;
	private User myUser;
	private String myMessage;
	private Element myElement;
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
		return myUser.getUsername();
	}
	
	/**
	 * Grants access to the User object for subclasses.
	 * @return
	 */
	protected User getUser() {
		return myUser;
	}
	
	public String getMessage() {
		return myMessage;
	}
	
	/**
	 * For use by SessionController to assign a HTML Element to this request.
	 * When the request is accepted or denied the element is modified
	 * by the request object.
	 * @param e - the element to be associated with this request.
	 */
	void setHTMLElement(Element e) {
		myElement = e;
	}
	
	/**
	 * Grants access to the HTML Element object for subclasses.
	 * @param e
	 * @return
	 */
	protected Element getMyElement() {
		return myElement;
	}
	
	/**
	 * Performs neccessary steps to accept the request. When
	 * this method is done, the Request should remove itself
	 * from its User's myRequests list.
	 * @param message User's response message to remote party.
	 */
	public abstract void accept(String message);
	
	/**
	 * Performs neccessary steps to deny the request. When
	 * this method is done, the Request should remove itself
	 * from its User's myRequests list.
	 * @param message User's response message to remote party.
	 */
	public abstract void deny(String message);
	
	/**
	 * This method is called by the internal timer when the request
	 * exceeds its lifetime. Implementations of the method should
	 * make a call to deny() along with disabling the accept() method.
	 * Additionally the Request instance should remove itself from
	 * the myRequests list of the User.
	 */
	protected abstract void timeOut();
	//TODO: we should probably make a timeOutListener interface so the popup
	//windows can display an error message if the request times out.
	
	/* TODO: add a kill() method which disables the request silently.
	 * this method will be used when we lose connection to a User, in
	 * order to disable the GUI or someone else from accepting the Request.
	 */
	
	/**
	 * This method is called when the connection to the owning user has been
	 * lost and should disable the request from performing any new actions
	 * or timeing out.
	 */
	protected abstract void kill();
	
	/**
	 * Disables the possibility for this Request to time out.
	 * Any implementation of accept() or deny() in subclasses
	 * should call this method!
	 */
	public void disableTimeOut() {
		timeOutTimer.stop();
	}
	
	public void actionPerformed(ActionEvent e) {
		timeOut();
	}
	
	public void addObserver(RequestObserver o) {
		observers.add(o);
	}
	
	public void removeObserver(RequestObserver o) {
		observers.remove(o);
	}
	
	protected RequestObserver[] getObservers() {
		return observers.toArray(new RequestObserver[0]);
	}
		
	/**
	 * This interface is provided for view components which display requests,
	 * so that they can disable themselves if the request is timed out or
	 * killed.
	 * 
	 * @author thaggus
	 */
	public static interface RequestObserver{
		/**
		 * This method should show a message indicating that the request timed
		 * out, and disable the input methods of accepting the request.
		 */
		public void requestTimedOut();
		/**
		 * This method should inform the user that the request was disabled due
		 * to a connection problem.
		 */
		public void requestKilled();
	}
}
