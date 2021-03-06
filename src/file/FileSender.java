package file;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import crypto.*;

public class FileSender extends Progressor implements Runnable {

	/**
	 * This constant determines how large chunks of bytes are read
	 * (and encrypted) from the file at a time.
	 */
	public static final int FILE_CHUNK_SIZE = 128;
	private Socket mySocket;
	private FileInputStream fileStream;
	private Crypto myCrypto;
	private long bytesToSend;
	/** An internal flag used to abort the file transfer if requested. */
	private boolean abort = false;
	
	/**
	 * Opens the inputted file f, connects to the specified host and
	 * starts transmitting the file using the given crypto c (if non-null).
	 * @param f - the file to transmit.
	 * @param reciever - inetaddress of the reciever.
	 * @param port - the port no. to connect to.
	 * @param c - the crypto to use for encryption. May be null: if so, the
	 * file is sent unencrypted.
	 * @throws FileNotFoundException - if the file to transmit is invalid.
	 * @throws IOException - if there is a problem connecting to the reciever.
	 */
	public FileSender(File f, String reciever, int port, Crypto c) 
			throws FileNotFoundException, IOException {
		Thread th = new Thread(this);
		fileStream = new FileInputStream(f);
		mySocket = new Socket(reciever, port);
		myCrypto = c;
		bytesToSend = f.length();
		th.start();
	}
	
	@Override
	/**
	 * File sending procedure. Called internally by constructor.
	 */
	public void run() {
		OutputStream socketOut = null;
		byte[] bytesIn = new byte[FILE_CHUNK_SIZE];
		int nBytesRead;
		long totalBytesSent = 0;
		int progress = -1; //-1 to make the first loop call observers with 0
		try {
			socketOut = mySocket.getOutputStream();
			nBytesRead = fileStream.read(bytesIn);
			while (nBytesRead > 0) {
				if (abort) {
					throw new IOException("File transfer aborted");
				}
				if (myCrypto != null) {
					/* If we are using crypto, encrypt the bytes */
					bytesIn = myCrypto.encrypt(bytesIn);
				}
				if (nBytesRead < FILE_CHUNK_SIZE) {
					byte[] bytesIn2 = new byte[nBytesRead];
					for (int i = 0; i < nBytesRead; i++) {
						bytesIn2[i] = bytesIn[i];
					}
					bytesIn = bytesIn2;
				}
				socketOut.write(bytesIn);
				totalBytesSent += nBytesRead;
				/* Check our progress and update observers if we incremented */
				if ((int) (totalBytesSent * 100.0 / bytesToSend)
						> progress) {
					progress = (int) (totalBytesSent * 100.0 / bytesToSend);
					updateObserversOnProgress(progress);
				}
				/* Read the next chunk */
				bytesIn = new byte[FILE_CHUNK_SIZE];
				nBytesRead = fileStream.read(bytesIn);
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			signalNormalTermination();
		} catch (IOException e) {
			String errorMsg = "Error sending file.";
			signalErrorToObservers(new Exception(errorMsg, e));
		} finally {
			try {
				if (mySocket != null) {
					mySocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fileStream != null) {
					fileStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	/**
	 * Aborts the file sending and closes all resources. Useful if, for
	 * example, the user decides to cancel the abort or the session is
	 * terminated by user.
	 */
	public void abort() {
		abort = true;
	}
}
