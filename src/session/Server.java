package session;

import java.awt.Color;
import java.net.ServerSocket;

import javax.swing.text.Element;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Server extends SessionController implements Runnable {

	private ServerSocket serverSocket;
	
	public Server(String myUsername, Color myColor) {
		super(myUsername, myColor);
		// TODO Implement
	}
	
	public void run() {
		// TODO: Implement
		throw new NotImplementedException();
	}
	
	/**
	 * Request class for incoming connections.
	 * @author thaggus
	 */
	public class IncomingConnectionRequest extends ConnectionRequest {

		private User myUser;
		private String myMessage;
		private Element myElement;
		
		public IncomingConnectionRequest(User userIn, String messageIn,
											Element htmlElementIn) {
			myUser = userIn;
			myMessage = messageIn;
			myElement = htmlElementIn;
		}
		
		@Override
		public void accept(String message) {
			//TODO: send the proper XML message to the user
			connectedUsers.add(myUser);
			notifyObservers();
			//done
		}

		@Override
		public void deny(String message) {
			//TODO: send the proper XML message to the user
			myUser.disconnect();
			//done
		}

		@Override
		protected void timeOut() {
			//TODO: modify the HTML Element
			//TODO: write a message in the chat log
			deny("Request timed out.");
			//done
		}
	}
}
