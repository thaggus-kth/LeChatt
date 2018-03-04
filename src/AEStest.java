import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

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
		String str = "hej och hallå!";
		// Skapa nyckel
		keyContent = generateKey();
			String e = encrypt(str);
			System.out.println("Encrypted: " + e);		
			String d = decrypt(e);
			//Avkryptera
			System.out.println("Decrypted: " + d);
			
			
		}
	
	public static byte[] generateKey() throws NoSuchAlgorithmException {
		KeyGenerator AESgen = KeyGenerator.getInstance("AES");
		AESgen.init(128);
		SecretKeySpec AESkey = (SecretKeySpec) AESgen.generateKey();
		byte[] key = AESkey.getEncoded();
		return key;
	}
	
	public static String encrypt(String plainText) throws Exception {
//		byte[] encryptMe = Base64.getMimeDecoder().decode(plainText);
		AEScipher = Cipher.getInstance("AES");
		SecretKeySpec AESkey = new SecretKeySpec(keyContent, "AES");
		AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);
		byte[] cipherData = AEScipher.doFinal(plainText.getBytes());
		byte[] encrypted =  Base64.getEncoder().encode(cipherData);
		return new String(encrypted);
	}
	
	public static void setKey(byte[] newKey) {
		keyContent = newKey;
	}
	
	public static String decrypt(String cipher) throws Exception {
		byte[] decryptMe = Base64.getDecoder().decode(cipher.getBytes());
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

	
	
	


