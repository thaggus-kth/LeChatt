package session;

/**
 * Class that groups Incoming and Outgoing connection requests into a 
 * common type. This class adds no functionality of its own: it is empty.
 * @author thaggus
 *
 */
public abstract class ConnectionRequest extends Request {

	public ConnectionRequest(User user, String message) {
		super(user, message);
	}
	
	/* Class intentionally left blank */
}
