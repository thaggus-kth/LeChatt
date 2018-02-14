package session;

/**
 * Interface for classes (typically view objects) which wish to observe the
 * SessionController object for changes which should be displayed to the user
 * @version 2018-02-14
 * @author thaggus
 *
 */
public interface ChatObserver {
	
	public void updateView();
}
