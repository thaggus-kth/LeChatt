package session;

import crypto.CryptoType;

/**
 * Superclass for file requests.
 * @author thaggus
 *
 */
public abstract class FileRequest extends Request {
	
	private CryptoType cryptoType;

	
	public FileRequest(User user, String message, CryptoType ct) {
		super(user, message);
		cryptoType = ct;
	}
	
	/**
	 * Gets the file name.
	 * @return name of file.
	 */
	public abstract String getFileName();
	
	/**
	 * Gets the file size in bytes.
	 * @return file size (in bytes).
	 */
	public abstract long getFileSize();
	
	/**
	 * Gets the encryption type used for transmitting the file.
	 * @return the encryption type used for transmitting the file.
	 */
	public CryptoType getCryptoType() {
		return cryptoType;
	}
}
