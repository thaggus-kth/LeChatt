package crypto;

/**
 * Enumeration of supported crypto types.
 * @author thaggus
 *
 */
public enum CryptoType {
	
	/**
	 * Plaintext, i.e. no encryption.
	 */
	PLAIN,
	
	/**
	 * Caesar cipher encryption.
	 */
	CAESAR,
	
	/**
	 * AES encryption.
	 */
	AES;
}
