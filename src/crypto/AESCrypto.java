package crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class AESCrypto implements Crypto {
	private static final CryptoType TYPE = CryptoType.AES;
	private Cipher AESCipher;
	private static byte[] keyContent;
	
	public AESCrypto(String key) {
		setKey(key);
	}
	
	public CryptoType getType() {
		return TYPE;
	}
	
	public void setKey(String newHexKey) {
		keyContent = hexStringToByteArray(newHexKey);
	}
	
	public byte[] generateKey() {
		byte[] key = null;
		try {
			KeyGenerator AESgen = KeyGenerator.getInstance("AES");
			AESgen.init(128);
			SecretKeySpec AESkey = (SecretKeySpec) AESgen.generateKey();
			key = AESkey.getEncoded();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return key;
	}
	
	public byte[] encrypt(byte[] plainBytes) {
		byte[] encryptedBytes = null;
		try {
			AESCipher = Cipher.getInstance("AES");
			SecretKeySpec AESkey = new SecretKeySpec(keyContent, "AES");
			AESCipher.init(Cipher.ENCRYPT_MODE, AESkey);
			encryptedBytes = AESCipher.doFinal(plainBytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | 
				IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptedBytes;
	}
	
	public byte[] decrypt(byte[] encryptedBytes) {
		byte[] decryptedBytes = null;
		try {
			SecretKeySpec AESkey = new SecretKeySpec(keyContent, "AES");
			AESCipher.init(Cipher.DECRYPT_MODE, AESkey);
			decryptedBytes = AESCipher.doFinal(encryptedBytes);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decryptedBytes;
	}


	
	public String encrypt(String plainText) {
		byte[] encryptedBytes = null;
		try {
			AESCipher = Cipher.getInstance("AES");
			SecretKeySpec AESkey = new SecretKeySpec(keyContent, "AES");
			AESCipher.init(Cipher.ENCRYPT_MODE, AESkey);
			encryptedBytes = Base64.getEncoder().encode(AESCipher.
					doFinal(plainText.getBytes()));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | 
				IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(byteArrayToHex(encryptedBytes));
	}
	
	public String decrypt(String encrypted) {
		byte[] decrypted = null;
		byte[] hexEncrypted = hexStringToByteArray(encrypted);
		byte[] decryptMe = Base64.getDecoder().decode(hexEncrypted);
		try {
			SecretKeySpec AESkey = new SecretKeySpec(keyContent, "AES");
			AESCipher.init(Cipher.DECRYPT_MODE, AESkey);
			decrypted = AESCipher.doFinal(decryptMe);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(decrypted);
	}
	
	public String byteArrayToHex(byte[] byteArray) {
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
