package file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import crypto.*;

public class FileReciever extends Progressor implements Runnable {
	
	private ServerSocket myServerSocket;
	private FileOutputStream fileOut;
	private Crypto myCrypto;
	/** The expected file size of the incoming file. */
	private long bytesToRecieve;
	/** An internal flag used to abort the file transfer if requested. */
	private boolean abort = false;
	
	/**
	 * Starts file recieving procedure. Opens a network connection and awaits
	 * the sender's connection.
	 * @param pathToWrite - the destination path of the incoming file.
	 * @param crypto - if non-null, decrypts the file using this crypto.
	 * @param fileSize - the expected size of the incoming file.
	 * @throws IOException if there was an error opening the network connection
	 * or accessing the specified destination filepath.
	 */
	public FileReciever(File pathToWrite, Crypto crypto, long fileSize) throws IOException {
		Thread th = new Thread(this);
		/* port = 0 makes the os select an available port automatically */
		myServerSocket = new ServerSocket(0);
		fileOut = new FileOutputStream(pathToWrite);
		myCrypto = crypto;
		bytesToRecieve = fileSize;
		th.start();
	}
	
	@Override
	/**
	 * File recieving procedure. Called internally by constructor.
	 */
	public void run() {
		Socket socket = null;
		InputStream socketIn = null;
		long totalBytesRecieved = 0;
		byte[] bytesIn = new byte[FileSender.FILE_CHUNK_SIZE];
		int progress = -1; //-1 to make the first loop call observers with 0
		int nBytesRead;
		try {
			/* Accept incoming connection */
			socket = myServerSocket.accept();
			socketIn = socket.getInputStream();
			/* Read bytes from Socket's stream */
			nBytesRead = socketIn.read(bytesIn);
			while (nBytesRead > 0) {
				if (abort) {
					throw new IOException("File transfer aborted");
				}
				if (myCrypto != null) {
					bytesIn = myCrypto.decrypt(bytesIn);
				}
				if (nBytesRead < FileSender.FILE_CHUNK_SIZE) {
					byte[] bytesIn2 = new byte[nBytesRead];
					for (int i = 0; i < nBytesRead; i++) {
						bytesIn2[i] = bytesIn[i];
					}
					bytesIn = bytesIn2;
				}
				fileOut.write(bytesIn);
				totalBytesRecieved += nBytesRead;
				/* Check our progress and update observers if we incremented */
				if ((int) (totalBytesRecieved * 100.0 / bytesToRecieve)
						> progress) {
					progress = 
						(int) (totalBytesRecieved * 100.0 / bytesToRecieve);
					updateObserversOnProgress(progress);
				}
				/* Read the next chunk */
				bytesIn = new byte[FileSender.FILE_CHUNK_SIZE];
				nBytesRead = socketIn.read(bytesIn);
				//TODO: debug
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			fileOut.flush();
			/* Check if we recieved the expected amount of bytes */
			if (totalBytesRecieved != bytesToRecieve) {
				String errorMsg = String.format("Expected %d bytes, recieved %d. "
						+ "The file might have been corrupted.", bytesToRecieve,
						totalBytesRecieved);
				signalErrorToObservers(new Exception(errorMsg));
			} else {
				signalNormalTermination();
			}
		} catch (IOException e) {
			String errorMsg = "Error recieveing file.";
			signalErrorToObservers(new Exception(errorMsg, e));
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fileOut != null) {
					fileOut.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets the port that is listening for incoming connections.
	 * @return the port
	 */
	public int getPort() {
		return myServerSocket.getLocalPort();
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
