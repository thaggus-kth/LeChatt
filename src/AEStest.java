import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import crypto.CryptoType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import javax.crypto.SecretKey;

import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;



public class AEStest {
	private static final CryptoType TYPE = CryptoType.AES;
	private static Cipher AEScipher;
	private static byte[] keyContent;

	public static void main(String[] args) throws Exception {
		String str = "hej";
		// Skapa nyckel
		keyContent = generateKey();
			
			System.out.println("Encrypted: " + new String(encrypt(str.getBytes())));			
			
			//Avkryptera
			System.out.println("Decrypted: " + new String(decrypt(encrypt(str.getBytes()))));
			
			
		}
	
	public static byte[] generateKey() throws NoSuchAlgorithmException {
		KeyGenerator AESgen = KeyGenerator.getInstance("AES");
		AESgen.init(128);
		SecretKeySpec AESkey = (SecretKeySpec) AESgen.generateKey();
		byte[] key = AESkey.getEncoded();
		return key;
	}
	
	public static byte[] encrypt(byte[] bytePlainText) throws Exception {
		AEScipher = Cipher.getInstance("AES");
		SecretKeySpec AESkey = new SecretKeySpec(keyContent, "AES");
		AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);
		byte[] cipherData = AEScipher.doFinal(bytePlainText);
		return cipherData;
	}
	
	public static void setKey(byte[] newKey) {
		keyContent = newKey;
	}
	
	public static byte[] decrypt(byte[] byteCipher) throws Exception {
		SecretKeySpec AESkey = new SecretKeySpec(keyContent, "AES");
		AEScipher.init(Cipher.DECRYPT_MODE, AESkey);
		byte[] bytePlainText = AEScipher.doFinal(byteCipher);
		return bytePlainText;

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

	
	
	


