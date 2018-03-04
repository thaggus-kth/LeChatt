package session;

import crypto.*;

public class OutgoingKeyRequest extends KeyRequest {

	private boolean keyWasSet = false;
	private int key = 0;
	private byte[] AESkey = null;
	
	public OutgoingKeyRequest(User user, String message, CryptoType ct) {
		super(user, message, ct);
	}
	
	@Override
	/**
	 * Creates a new Crypto and puts it in the User's myCryptos list.
	 * NOTE: you MUST call the setKey method with the key recieved by
	 * the remote user before calling this method.
	 * @throws NoKeyException if the key has not been set
	 */
	public void accept(String message) throws NoKeyException {
		Crypto newCrypto = null;
		if (!keyWasSet) {
			throw new NoKeyException();
		}
		switch (getCryptoType()) {
		case AES:
			newCrypto = new AESCrypto(AESkey);
			break;
		case CAESAR:
			newCrypto = new CaesarCrypto(key);
		}
		getUser().myCryptos.put(getCryptoType(), newCrypto);
		getUser().fireUserNotificationEvent("(name) accepted"
				+ "your key request for (ct). you can now select (ct) from the"
				+ "crypto list!");
	}
	
	/**
	 * Use this method to pass the key which we recieved from the remote
	 * user to the request. This method must be called before accept, or
	 * else accept will throw an exception!
	 * @param key
	 */
	public void setCaesarKey(int key) {
		this.key = key;
		keyWasSet = true;
	}
	
	public void setAESKey(byte[] key) {
		AESkey = key;
		keyWasSet = true;
	}

	@Override
	public void deny(String message) {
		getUser().fireUserNotificationEvent("your key request to (name)"
				+ "was refused"); //TODO: fix
		clean();
	}

	@Override
	protected void timeOut() {
		for (Request.RequestObserver o : getObservers()) {
			o.requestTimedOut();
		}
		clean();
	}

	@Override
	protected void kill() {
		for (Request.RequestObserver o : getObservers()) {
			o.requestKilled();
		}
		clean();
	}
	
	public class NoKeyException extends RuntimeException {
		
		NoKeyException() {
			super("A key must be set using setKey(int)"
				+ " method before calling accept().");
		}
	}
	
	/**
	 * Performs cleanup work when this request should be removed.
	 */
	private void clean() {
		disableTimeOut();
		getUser().myRequests.remove(this);
	}

}
