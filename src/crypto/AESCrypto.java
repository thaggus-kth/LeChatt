package crypto;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypto {
	private static final CryptoType TYPE = CryptoType.AES;
	private Cipher AESCipher;
	SecretKeySpec AESkey;
	
	public AESCrypto() {
		KeyGenerator AESgen;
		try {
			AESgen = KeyGenerator.getInstance("AES");
			SecretKeySpec newKey = (SecretKeySpec) AESgen.generateKey();
			setKey(AESkey);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public CryptoType getType() {
		return TYPE;
	}
	
	public void setKey(SecretKeySpec newKey) {
		AESkey = newKey;
	}

	
	public String encrypt(String message) {
		byte[] dataToEncrypt = message.getBytes();
		byte[] cipherData;
		String encrypted = new String();
		try {
			AESCipher = Cipher.getInstance("AES");
			AESCipher.init(Cipher.ENCRYPT_MODE, AESkey);		
			cipherData = AESCipher.doFinal(dataToEncrypt);
			encrypted = new String(cipherData, StandardCharsets.UTF_8);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return encrypted;
	}
	
	public String decrypt(String message) {
		byte[] keyContent = AESkey.getEncoded();
		String decrypted = new String();
		return decrypted;
	}
	


}
