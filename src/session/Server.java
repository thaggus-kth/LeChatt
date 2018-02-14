package session;

import java.awt.Color;
import java.net.ServerSocket;

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
	
	
}
