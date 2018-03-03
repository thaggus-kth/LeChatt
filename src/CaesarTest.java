import java.util.ArrayList;

public class CaesarTest {
	public static int key = 140;

	public static void main(String[] args) {
//		String fin = "Hej på er! Jag är 27 år?";
//		System.out.println(encrypt(fin));
//		System.out.println(decrypt(encrypt(fin)));
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte) 127;
		byteArray[1] = (byte) 1;
		byteArray[2] = (byte) 0;
		byteArray[3] = (byte) -23;
		for (Byte b : byteArray) {
			System.out.println(b.intValue());
		}
		byte [] e = encrypt(byteArray);
		for (Byte b : e) {
			System.out.println(b.intValue());
		}
		byte[] d = decrypt(e);
		for (Byte b : d) {
			System.out.println(b.intValue());
		}
		System.out.println((byte) 1);
		System.out.println((byte) 512);
		
		
	}
	
	public static String encrypt(String message) {
		String encrypted = new String();
		char[] charArray = message.toCharArray();
		for (char c : charArray) {
			encrypted += (char) ((c + key) % 65533);
		}
		return encrypted;
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
	
	public static String decrypt(String message) {
		String decrypted = new String();
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
}
	