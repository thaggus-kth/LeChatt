import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import crypto.CryptoType;

public class AEStest {
	private static final CryptoType TYPE = CryptoType.AES;
	private static Cipher AEScipher;
	private static SecretKeySpec AESkey;

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] dataToEncrypt = "Hej".getBytes();
		
		// Skapa nyckel
			KeyGenerator AESgen = KeyGenerator.getInstance("AES");
			AESgen.init(128);
			AESkey = (SecretKeySpec)AESgen.generateKey();			
			
			// Kryptera
			AEScipher = Cipher.getInstance("AES");
			AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);
			byte[] cipherData = AEScipher.doFinal(dataToEncrypt);
			System.out.println("Encrypted: " + new String(cipherData));
			System.out.println(decrypt(new String(cipherData)));
			
			
		}
	
	public static String decrypt(String message) {
		byte[] keyContent = AESkey.getEncoded();
		String decrypted = new String();

		SecretKeySpec decodeKey = new SecretKeySpec(keyContent, "AES");
		try {
			AEScipher.init(Cipher.DECRYPT_MODE, decodeKey);
			byte[] decryptedData = AEScipher.doFinal(message.getBytes());
			System.out.println("Decrypted: " + new String(decryptedData));
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decrypted;
	}

	}
	
	
	


