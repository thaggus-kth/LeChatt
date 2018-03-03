import java.util.ArrayList;

public class CaesarTest {
	public static int key = 256;

	public static void main(String[] args) {
//		String fin = "Hej på er! Jag är 27 år?";
//		System.out.println(encrypt(fin));
//		System.out.println(decrypt(encrypt(fin)));
		ArrayList<Byte> byteArray = new ArrayList<Byte>();
		byteArray.add((byte) 127);
		byteArray.add((byte) -5);
		byteArray.add((byte) -127);
		byteArray.add((byte) 0);
		System.out.println(byteArray);
		System.out.println(encrypt(byteArray));
		System.out.println(decrypt(encrypt(byteArray)));
		
		
	}
	
	public static String encrypt(String message) {
		String encrypted = new String();
		char[] charArray = message.toCharArray();
		for (char c : charArray) {
			encrypted += (char) ((c + key) % 65533);
		}
		return encrypted;
		}
	
	public static ArrayList<Byte> encrypt(ArrayList<Byte> byteArray) {
		ArrayList<Byte> encryptedFile = new ArrayList<Byte>(); 
		for (Byte b : byteArray) {
			byte encrByte = (byte) (((b.intValue() + key + 127) % 256) - 127);
			encryptedFile.add(encrByte);
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
	
	public static ArrayList<Byte> decrypt(ArrayList<Byte> byteArray) {
		ArrayList<Byte> decryptedFile = new ArrayList<Byte>(); 
		for (Byte b : byteArray) {
			byte decrByte = (byte) (((b.intValue() + 127 - key + 256) % 256) - 127);
			decryptedFile.add(decrByte);
		}
		return decryptedFile;
		}
}
	