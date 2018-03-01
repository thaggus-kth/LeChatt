package crypto;

import java.util.Random;

public class CaesarCrypto {
	private int key = 0;
	private static CryptoType TYPE;
	
	public CaesarCrypto(int key) {
		setKey(key);
	}
	
	public String encrypt(String message) {
		String encrypted = new String();
		char[] charArray = message.toCharArray();
		for (char c : charArray) {
			encrypted += (char) ((c + key) % 65533);
		}
		return encrypted;
	}
	
	public byte[] encrypt(byte[] b) {
		byte[] encryptedFile = new byte[0]; 
		return encryptedFile;
	}
	
	public String decrypt(String message) {
		String decrypted = new String();
		char[] charArray = message.toCharArray();
		for (char c : charArray) {
			decrypted += (char) ((c + 65533 - key) % 65533);
		}
		return decrypted;
	}
	public byte[] decrypt(byte[] b) {
		byte[] decrFile;
		return decrFile;
	}
	
	/**
	 * Sets the encryption key
	 * @param newKey new encryption key
	 */
	public void setKey(int newKey) {
		key = newKey;
	}
	
	/**
	 * Returns the crypto type
	 * @return TYPE CryptoType of the crypto.
	 */
	public CryptoType getType() {
		return TYPE;
	}
	
	/**
	 * Returns a random Caesar Crypto key.
	 * @return random integer key between 1 and 29. 
	 */
	public static int randomCaesarKey() {
		Random rdm = new Random();
		int randomKey = rdm.nextInt(254) + 1; // a max value of key is chosen to not result in the same byte when encrypting bytes. 
		return randomKey;
	}

}
