package crypto;

public interface Crypto {
	
	public String encrypt(String message);
	public byte[] encrypt(byte[] bytes);
	public String decrypt(String message);
	public byte[] decrypt(byte[] bytes);
	public void setKey(int key);
	public CryptoType getType();
}
