package session;

import crypto.*;

public class IncomingKeyRequest extends KeyRequest {
	private String myMessage = null;
	private User myUser = null;
	private CryptoType ct = null;

	public IncomingKeyRequest(User user, String message, CryptoType ct) {
		super(user, message, ct);
		myUser = user;
		this.ct = ct;
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void accept(String message) {
		Crypto myCrypto = myUser.myCryptos.get(ct);
		String key = myCrypto.key;
		String acceptTag = "<keyRequest reply=\"yes\" type=\"" +
				getCryptoType().toString()+ "\"" + "key=\"" +
				message + "</request>";
		disableTimeOut();
		myUser.writeLine(acceptTag);
		//done
	}

	@Override
	public void deny(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void timeOut() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void kill() {
		// TODO Auto-generated method stub

	}
	
	public void setMessage(String messageIn) {
		if (myMessage != null) {
			throw new IllegalArgumentException("Message already set!");
		}
		myMessage = messageIn;
	}

}
