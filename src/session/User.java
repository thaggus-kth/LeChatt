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
		
	}
	
	public void User(String hostAddress, int port) {
		try {
            connection = new Socket(hostAddress, port);
            out = new PrintWriter(connection.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                        connection.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.\n" + e);
            System.exit(1);
        } catch (IOException e) {
        	System.err.println(e);
        	System.exit(1);
        }
		
		run();
	}
	
	public void run() {
		String userInput;
		while ((userInput = in.readLine()) != null) {
		    writeLine(userInput);
		}
		
	}
	
	public void writeLine(String message) {
		out.println(message);
	}
	
	public void disconnect() {
		out.println("disconnect");
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
	
	public void sendKeyRequest(CryptoType c, String message) {
		Request keyRequest = new OutgoingKeyRequest(60, message, this, c);
		myRequests.add(keyRequest);
	}
	
	public void sendFileRequest(File file, CryptoType c, String message) {
		Request fileRequest = new OutgoingFileRequest(60, message, this, file, c);
		myRequests.add(fileRequest);
	}
	
	public void setActiveCrypto(CryptoType c) {
		
	}
	
	public void sendConnectRequest(String message) {
		Request connectRequest = new OutgoingConnectionRequest(60, message, this);
		myRequests.add(connectRequest);
	}
	
	public void fireUserNotificationEvent(Message message) {
		
	}

}
