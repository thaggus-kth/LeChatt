package session;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.text.Element;
import javax.xml.stream.XMLOutputFactory;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Server extends SessionController implements Runnable {
	private ServerSocket serverSocket;
	private boolean running = true;
	
	public Server(String myUsername, Color myColor, int port) throws IOException {
		super(myUsername, myColor);
		Thread th = new Thread(this);
		try {
			serverSocket = new ServerSocket(port);
			th.start();
		} catch (IOException e) {
			System.err.println(e);
			throw e;
		}
	}
	
	public void run() {
		try {
			while (running) {
				Socket newSocket = serverSocket.accept();
				User newUser = new User(newSocket, this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	/**
	 * Distributes the message to all connected users and adds it to ChatLog.
	 */
	public void newMessage(Message m) {
		for (User u : connectedUsers) {
			if (u != m.getSource()) {
				u.sendMessage(m);
			}
		}
		super.newMessage(m);
	}
	
	@Override
	public void disconnect() {
		super.disconnect();
		try {
			if (!serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
	/**
	 * Request class for incoming connections.
	 * @author thaggus
	 */
	public class IncomingConnectionRequest extends ConnectionRequest {
		
		/* Since we want to instantiate this class before the message
		 * is known, we overshadow the myMessage variable and getMessage
		 * method in abstract Request.
		 * 
		 * In the User class, immediately after the creation of an
		 * IncomingConnectionRequest the User goes on to read the socket
		 * stream, which should immediately contain a message in normal
		 * cases, so the field will be quickly filled out.
		 */
		private String myMessage = null;
		
		
		public IncomingConnectionRequest(User userIn) {
			super(userIn, null);
		}
		
		/**
		 * Accepts the connection request from the remote user
		 * and adds them to the chat session.
		 * @param message Optional message to send to remote user.
		 */
		@Override
		public void accept(String message) {
			String acceptTag = "<request reply=\"yes\">" + message + "<\request>";
			getUser().writeLine(acceptTag);
			connectedUsers.add(getUser());
			notifyObservers();
			getUser().myRequests.remove(this);
			//done
		}
		
		/**
		 * Denies the connection request from the remote user
		 * and closes the connection to them.
		 * @param message Optional message to send to remote user.
		 */
		@Override
		public void deny(String message) {
			String denyTag = "<request reply=\"no\">" + message + "<\request>";
			getUser().writeLine(denyTag);
			getUser().disconnect();
			getUser().myRequests.remove(this);
			//done
		}

		@Override
		protected void timeOut() {
			//TODO: modify the HTML Element
			//TODO: write a message in the chat log
			deny("Request timed out.");
			//done
		}
		
		/**
		 * Gets the message sent by the remote user (who wants to connect).
		 * Note that this may not be available immediately after class
		 * instantiation, since it must be recieved via the remote
		 * connection.
		 * @return the message if it is available, an empty string if
		 * it is not.
		 */
		@Override
		public String getMessage() {
			return (myMessage != null) ? myMessage : "";
		}
		
		/**
		 * Since the message we recieve from a new user is not known
		 * at creation time, this package-private method is provided
		 * for use by User object only.
		 * @param messageIn the message to set.
		 */
		void setMessage(String messageIn) {
			if (myMessage != null) {
				throw new IllegalArgumentException("Message already set!");
			}
			myMessage = messageIn;
		}
	}
}
