package crypto;

public interface Crypto {
	
	public byte[] encrypt(byte[] bytes);
	public byte[] decrypt(byte[] bytes);
	public CryptoType getType();
}
