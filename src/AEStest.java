import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import crypto.CryptoType;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

import javax.crypto.SecretKey;

import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;




public class AEStest {
	private static final CryptoType TYPE = CryptoType.AES;
	private static Cipher AEScipher;
	private static byte[] keyContent;

	public static void main(String[] args) throws Exception {
		keyContent = generateKey();
		

		byte[] byteArray = new byte[4];
		byteArray[0] = (byte) 127;
		byteArray[1] = (byte) 1;
		byteArray[2] = (byte) 0;
		byteArray[3] = (byte) -23;
		System.out.println("Bytes");
		for (Byte b : byteArray) {
			System.out.println(b.intValue());
		}
		byte [] enc = encrypt(byteArray);
		System.out.println("Encrypted Bytes");
		for (Byte b : enc) {
			System.out.println(b.intValue());
		}
		byte[] dec = decrypt(enc);
		System.out.println("Decrypted Bytes");
		for (Byte b : dec) {
			System.out.println(b.intValue());
		}
		//String str = "Hur är laget?";
			//String e = encrypt(str);
			//System.out.println("Encrypted: " + new String(e));		
			//String d = decrypt(e);
			//Avkryptera
			//System.out.println("Decrypted: " + d);
			
			
		}
	
	public static byte[] generateKey() throws NoSuchAlgorithmException {
		KeyGenerator AESgen = KeyGenerator.getInstance("AES");
		AESgen.init(128);
		SecretKeySpec AESkey = (SecretKeySpec) AESgen.generateKey();
		byte[] key = AESkey.getEncoded();
		return key;
	}
	public static byte[] encrypt(byte[] plainText) throws Exception {
		AEScipher = Cipher.getInstance("AES");
		SecretKeySpec AESkey = new SecretKeySpec(keyContent, "AES");
		AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);
		byte[] cipherData = AEScipher.doFinal(plainText);
		return cipherData;
	}
	
	public static byte[] decrypt(byte[] cipher) throws Exception {
		SecretKeySpec AESkey = new SecretKeySpec(keyContent, "AES");
		AEScipher.init(Cipher.DECRYPT_MODE, AESkey);
		byte[] bytePlainText = AEScipher.doFinal(cipher);
		return bytePlainText;
	}
	
	public static String encrypt(String plainText) throws Exception {
		AEScipher = Cipher.getInstance("AES");
		SecretKeySpec AESkey = new SecretKeySpec(keyContent, "AES");
		AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);
		byte[] cipherData = AEScipher.doFinal(plainText.getBytes());
		byte[] encrypted =  Base64.getEncoder().encode(cipherData);
		return new String(byteToHex(encrypted));
	}
	
	
	public static String byteToHex(byte[] byteArray) {
	    String hexString = DatatypeConverter.printHexBinary(byteArray);
	    return hexString;
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
	
	
	public static void setKey(byte[] newKey) {
		keyContent = newKey;
	}
	
	public static String decrypt(String cipher) throws Exception {
		byte[] hex = hexStringToByteArray(cipher);
		byte[] decryptMe = Base64.getDecoder().decode(hex);
		SecretKeySpec AESkey = new SecretKeySpec(keyContent, "AES");
		AEScipher.init(Cipher.DECRYPT_MODE, AESkey);
		byte[] bytePlainText = AEScipher.doFinal(decryptMe);
		return new String(bytePlainText);

//		try {
//			AEScipher.init(Cipher.DECRYPT_MODE, decodeKey);
//			byte[] decryptedData;
//			byte[] m = message.getBytes();
//			System.out.println(m.length);
//			decryptedData = AEScipher.doFinal(m);
//			decrypted = new String(decryptedData);
//			} catch (InvalidKeyException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalBlockSizeException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (BadPaddingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		
//		return decrypted;

	}
}

	
	
	


