package crypto;

import java.util.ArrayList;
import java.util.Random;

public class CaesarCrypto {
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
	
	public ArrayList<Byte> encrypt(ArrayList<Byte> byteArray) {
		ArrayList<Byte> encryptedFile = new ArrayList<Byte>(); 
		for (Byte b : byteArray) {
			byte encrByte = (byte) (((b.intValue() + key + 127) % 256) - 127);
			encryptedFile.add(encrByte);
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
	public ArrayList<Byte> decrypt(ArrayList<Byte> byteArray) {
		ArrayList<Byte> decryptedFile = new ArrayList<Byte>(); 
		for (Byte b : byteArray) {
			byte decrByte = (byte) (((b.intValue() + 127 - key + 256) % 256) - 127);
			decryptedFile.add(decrByte);
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
		int randomKey = rdm.nextInt(254) + 1; // a max value of key is chosen to not result in the same byte when encrypting bytes. 
		return randomKey;
	}

}
