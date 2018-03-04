package crypto;

public interface Crypto {
	
	public byte[] encrypt(byte[] bytes);
	public byte[] decrypt(byte[] bytes);
	public String encrypt(String text);
	public String decrypt(String text);
	public CryptoType getType();
	public void setKey(String hexKey);
	public String getKey();
}
