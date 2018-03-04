package session;

import crypto.*;
import session.OutgoingKeyRequest.NoKeyException;

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
		Crypto newCrypto = null;
		switch (getCryptoType()) {
		case PLAIN:
			break;
		case AES:
			newCrypto = new AESCrypto();
			break;
		case CAESAR:
			newCrypto = new CaesarCrypto();
			break;
		}
		getUser().myCryptos.put(getCryptoType(), newCrypto);
		getUser().setActiveCrypto(newCrypto.getType());
		String acceptTag = "<keyrequest reply=\"yes\" type=\"" +
				getCryptoType().toString() + "\"" + " key=\"" + newCrypto.getKey() + "\"" + ">" +
				message + "</keyrequest>"; 
		disableTimeOut();
		myUser.writeLine(acceptTag);
		myUser.fireUserNotificationEvent(String.format(
				"You can now choose %s encryption for user %s",
				getCryptoType(), getUsername()));
		clean();
	}

	@Override
	public void deny(String message) {
		getUser().writeLine("<keyrequest reply=\"no\">" + message
				+ "</keyrequest>");
		clean();
	}

	@Override
	protected void timeOut() {
		String msg = String.format("The request from %s to receive a crypto key, timed out.", getUser().getUsername());
		getUser().fireUserNotificationEvent(msg);
		clean();
	}

	@Override
	protected void kill() {
		//TODO: implement?
	}
	
	public void setMessage(String messageIn) {
		if (myMessage != null) {
			throw new IllegalArgumentException("Message already set!");
		}
		myMessage = messageIn;
	}
	
	private void clean() {
		disableTimeOut();
		getUser().myRequests.remove(this);
	}

}
