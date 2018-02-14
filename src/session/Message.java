package session;

public class Message {
	private User source;
	private String message;
	
	public void Message(String m, User u) {
		message = m;
		source = u;
	}
	
	public User getSource() {
		return source;
	}
	
	public String getMessage() {
		return message;
	}

}
