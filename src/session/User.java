package session

import java.io.*;
import java.net.*;
import java.util.*;

public class User implements Runnable {
	private Socket connection;
	private boolean connected;
	Request[] myRequests;
	Crypto[] myCryptos;
	String username;
	private Crypto activeCrypto;
	private ConnectionObserver[] observers;
	private OutputStream out;
	private InputStream in;
	
	public void run() {
		in = connection.getInputStream();
		out = connection.getOutputStream();
		
	}
	
	public void writeLine(String message) {
		
	}
	
	public void disconnect() {
		
	}
	
	public void addObserver(ConnectionObserver o) {
		
	}
	
	public void fireMessageEvent(Message m) {
		for (connectionObserver o : observers) {
			o.newMessage(m);
		}
	}
	
	public void fireNewRequestEvent(Request r) {
		for (connectionObserver o : observers) {
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
