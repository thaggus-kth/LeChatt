package session;

import crypto.CryptoType;

/**
 * Abstract class for key requests. Tracks the type of crypto to use.
 * @author thaggus
 *
 */
public abstract class KeyRequest extends Request {

	private CryptoType cryptoType;
	
	public KeyRequest(User user, String message, CryptoType ct) {
		super(user, message);
		cryptoType = ct;
	}
	
	public CryptoType getCryptoType() {
		return cryptoType;
	}

}
