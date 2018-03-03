package crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypto implements Crypto {
	private static final CryptoType TYPE = CryptoType.AES;
	private Cipher AESCipher;
	private static byte[] keyContent;
	
	public AESCrypto(byte[] key) {
		setKey(key);
	}
	
	public CryptoType getType() {
		return TYPE;
	}
	
	public void setKey(byte[] newKey) {
		keyContent = newKey;
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


	
	public byte[] encrypt(byte[] byteMessage) {
		byte[] encrypted = null;
		try {
			AESCipher = Cipher.getInstance("AES");
			SecretKeySpec AESkey = new SecretKeySpec(keyContent, "AES");
			AESCipher.init(Cipher.ENCRYPT_MODE, AESkey);
			encrypted = AESCipher.doFinal(byteMessage);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | 
				IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encrypted;
	}
	
	public byte[] decrypt(byte[] byteEncrypted) {
		byte[] decrypted = null;
		try {
			SecretKeySpec AESkey = new SecretKeySpec(keyContent, "AES");
			AESCipher.init(Cipher.DECRYPT_MODE, AESkey);
			decrypted = AESCipher.doFinal(byteEncrypted);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decrypted;

	}
	


}
