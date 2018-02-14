package session

import java.io.*;
import java.net.*;
import java.util.*;

public class User implements Runnable {
	private Socket connection;
	private boolean connected;
	ArrayList<Request> myRequests;
	ArrayList<Crypto> myCryptos;
	String username;
	private Crypto activeCrypto;
	private ArrayList<ConnectionObserver> observers;
	private PrintWriter out;
	private BufferedReader in;
	
	public void User(Socket mySocket) {
		Thread th = new Thread(this);
		connection = mySocket;
		th.start();
	}
	
	public void User(String hostAddress, int port) {
		Thread th = new Thread(this);
		try {
            connection = new Socket(hostAddress, port);
            out = new PrintWriter(connection.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                        connection.getInputStream()));
            th.start();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.\n" + e);
        } catch (IOException e) {
        	System.err.println(e);
        }
	}
	
	public void run() {
		String userInput;
		while ((userInput = in.readLine()) != null) {
		    
			//XML parsning 
			
			//meddelande: firemessage event
			//Request: fireRequest event
		}
	}
	
	/**
	 * sends XML formatted message over the internet.
	 * @param xmlFormattedMessage
	 */
	public void writeLine(String xmlFormattedMessage) {
		out.println(xmlFormattedMessage);
	}
	/**
	 * Closes the socket.
	 * Send a disconnect message with writeLine before calling this method.
	 */
	public void disconnect() {
		connection.close();
	}
	
	public void addObserver(ConnectionObserver o) {
		observers.add(o);
	}
	
	public void fireMessageEvent(Message m) {
		for (ConnectionObserver o : observers) {
			o.newMessage(m);
		}
	}
	
	public void fireNewRequestEvent(Request r) {
		for (ConnectionObserver o : observers) {
			o.newRequest(r);
		}
	}
	
	public void addRequest(Request r) {
		myRequests.add(r);
	}
	
//	public void sendKeyRequest(CryptoType c, String message) {
//		Request keyRequest = new OutgoingKeyRequest(Request.DEFAULT_LIFETIME, message, this, c);
//		myRequests.add(keyRequest);
//	}
	
//	public void sendFileRequest(File file, CryptoType c, String message) {
//		Request fileRequest = new OutgoingFileRequest(60, message, this, file, c);
//		myRequests.add(fileRequest);
//	}
	
	public void setActiveCrypto(CryptoType c) {
		switch (c) {
			case CAESAR:
				activeCrypto = CaesarCrypto;
			case AES:
				activeCrypto = AESCrypto;
		}
	}
	
//	public void sendConnectRequest(String message) {
//		Request connectRequest = new OutgoingConnectionRequest(60, message, this);
//		myRequests.add(connectRequest);
//	}
	
	public void fireUserNotificationEvent(Message message) {
		for (ConnectionObserver o : observers) {
			o.newNotification(message);
		}
	}

}
