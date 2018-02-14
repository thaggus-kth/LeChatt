package session;

/**
 * Interface for classes which wants to observe User objects and listen for
 * connection and request changes.
 * @author thaggus
 *
 */
public interface ConnectionObserver {
	
	public void newMessage(Message m);
	
	public void newRequest(Request r);
	
	/**
	 * This method is called when an active Request has some new
	 * information (e.g. the user responded to a key request). If
	 * the observer has a chat log, this method should write the
	 * message to the chat log. If not, this call should be ignored.
	 * @param message - the message to be written to the log.
	 */
	public void newNotification(Message m);
}
