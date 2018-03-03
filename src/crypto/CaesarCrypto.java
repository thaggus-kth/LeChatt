package crypto;

import java.util.ArrayList;
import java.util.Random;

public class CaesarCrypto implements Crypto {
	private int key = 0;
	private static final CryptoType TYPE = CryptoType.CAESAR;
	
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
	
	public byte[] encrypt(byte[] byteArray) {
		byte[] encryptedFile = new byte[byteArray.length]; 
		int i = 0;
		for (Byte b : byteArray) {
			byte encrByte = (byte) (((b.intValue() + key + 256) % 512) - 256);
			encryptedFile[i] = encrByte;
			i++;
		}
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
	public byte[] decrypt(byte[] byteArray) {
		byte[] decryptedFile = new byte[byteArray.length]; 
		int i = 0;
		for (Byte b : byteArray) {
			byte decrByte = (byte) (((b.intValue() + 256 - key + 512) % 512) - 256);
			decryptedFile[i] = decrByte;
			i++;
		}
		return decryptedFile;
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
		int randomKey = rdm.nextInt(255) + 1; // a max value of key is chosen to not result in the same byte when encrypting bytes. 
		return randomKey;
	}

}
