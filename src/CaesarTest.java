import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class CaesarTest {
	public static int key;

	public static void main(String[] args) throws UnsupportedEncodingException {
		//String hexKey = Integer.toHexString(5);
		int rkey = randomCaesarKey();
		String sKey = Integer.toHexString(rkey);
		
		setKey(sKey);
		String fin = "Hej på er! Jag är 27 år?";
////		
		System.out.println(encrypt(fin));
		System.out.println(decrypt(encrypt(fin)));
//		byte[] byteArray = new byte[4];
//		byteArray[0] = (byte) 127;
//		byteArray[1] = (byte) 1;
//		byteArray[2] = (byte) 0;
//		byteArray[3] = (byte) -23;
//		for (Byte b : byteArray) {
//			System.out.println(b.intValue());
//		}
//		byte [] e = encrypt(byteArray);
//		for (Byte b : e) {
//			System.out.println(b.intValue());
//		}
//		byte[] d = decrypt(e);
//		for (Byte b : d) {
//			System.out.println(b.intValue());
//		}
//		System.out.println((byte) 1);
//		System.out.println((byte) 512);
	}
	
	public static int randomCaesarKey() {
		Random rdm = new Random();
		int randomKey = rdm.nextInt(255) + 1; // a max value of key is chosen to not result in the same byte when encrypting bytes. 
		return randomKey;
	}
	
	public static void setKey(String newKey) {
		key = Integer.parseInt(newKey, 16);
	}
	
	public static String encrypt(String message) throws UnsupportedEncodingException {
		String encrypted = new String();
		char[] charArray = message.toCharArray();
		for (char c : charArray) {
			encrypted += (char) ((c + key) % 65533);
		}
		String hexEncrypted = byteArrayToHex(encrypted.getBytes());
		return hexEncrypted;
		}
	
	public static byte[] encrypt(byte[] byteArray) {
		byte[] encryptedFile = new byte[byteArray.length]; 
		int i = 0;
		for (Byte b : byteArray) {
			byte encrByte = (byte) (((b.intValue() + key + 256) % 512) - 256);
			encryptedFile[i] = encrByte;
			i++;
		}
		return encryptedFile;
		}
	
	public static String decrypt(String hexMessage) {
		String decrypted = new String();
		String message = new String(hexStringToByteArray(hexMessage));
		char[] charArray = message.toCharArray();
		for (char c : charArray) {
			decrypted += (char) ((c + 65533 - key) % 65533);
		}
		return decrypted;
	}
	
	public static byte[] decrypt(byte[] byteArray) {
		byte[] decryptedFile = new byte[byteArray.length]; 
		int i = 0;
		for (Byte b : byteArray) {
			byte decrByte = (byte) (((b.intValue() + 256 - key + 512) % 512) - 256);
			decryptedFile[i] = decrByte;
			i++;
		}
		return decryptedFile;
		}
	
	public static String byteArrayToHex(byte[] byteArray) {
	    String hex = DatatypeConverter.printHexBinary(byteArray);
	    return hex;
	}
	
	/**
	 * From https://stackoverflow.com/questions/140131/convert-a-string-
	 * representation-of-a-hex-dump-to-a-byte-array-using-java
	 * @param s
	 * @return
	 */
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}
	