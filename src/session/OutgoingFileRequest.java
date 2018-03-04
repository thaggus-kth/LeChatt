package session;

import file.*;
import crypto.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import crypto.CryptoType;

public class OutgoingFileRequest extends FileRequest
									implements ProgressObserver {

	private File myFile;
	private FileSender mySender;
	private ProgressorProxy progressorProxy = new ProgressorProxy();
	private Progressor progressor = progressorProxy;
	private static final int UNSPECIFIED_PORT = -1;
	private int port = UNSPECIFIED_PORT;
	/**
	 * Flag for checking if this Request is awaiting reply. There can only
	 * be one such file request at a time, otherwise the XML protocol breaks.
	 */
	private boolean awaitingReply = true;
	
	public OutgoingFileRequest(User user, String message, CryptoType ct,
			File f) {
		super(user, message, ct);
		myFile = f;
	}

	/**
	 * Sets the port to use when sending the file. This information is
	 * recieved in the fileresponse tag of the XML protocol, and must be
	 * passed into the request before calling accept().
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public String getFileName() {
		return myFile.getName();
	}

	@Override
	public long getFileSize() {
		return myFile.length();
	}
	
	/**
	 * Checks if this Request is awaiting reply. There can only be one such
	 * file request per User at a time, otherwise the XML protocol breaks.
	 */
	public boolean isAwaitingReply() {
		return awaitingReply;
	}

	@Override
	/**
	 * Call this method to indicate that the remote party accepted our file
	 * sending request. The request object will stick around until the file
	 * transfer is completed.
	 * NOTE that setPort() must be called with a valid port number (recieved
	 * in the fileresponse tag of the XML number) before calling this method!
	 * @throws NoPortSpecifiedException if the port to use was not specified
	 * via call to setPort before calling this method.
	 */
	public void accept(String message) throws NoPortSpecifiedException {
		User myUser = getUser();
		Crypto cryptoToUse = null;
		String notification = String.format("%s accepted your file %s",
				myUser.getUsername(), myFile.getName());
		if (message == null || message.isEmpty()) {
			notification += ".";
		} else {
			notification +=  " with the message \"" + message + "\"";
		}
		disableTimeOut();
		awaitingReply = false;
		
		if (port == UNSPECIFIED_PORT) {
			throw new NoPortSpecifiedException();
		}
		if (getCryptoType() != CryptoType.PLAIN) {
			cryptoToUse = myUser.myCryptos.get(getCryptoType());
		}
		getUser().fireUserNotificationEvent(notification);
		try {
			mySender = new FileSender(myFile, myUser.getInetAdress(false),
					port, cryptoToUse);
			mySender.addObserver(this);
			progressorProxy.realProgressorAvailable(mySender);
			progressor = mySender;
		} catch (IOException e) {
			String errMsg = String.format("Could not start transfer of file %s"
					+ "to user %s: " + e.getMessage() + "\nPlease try again.",
					myFile.getName(), myUser.getUsername()); 
			myUser.fireUserNotificationEvent(errMsg);
			clean();
		}
	}
	
	/**
	 * Grants access to the enclosed FileSender object via the Progressor
	 * interface, for visualization by view. Note that calling this before
	 * any call to accept() will return null.
	 * @return Progessor representation of the FileSender if the sending 
	 * process has started. Null otherwise.
	 */
	public Progressor getFileSender() {
		return progressor;
	}

	@Override
	public void deny(String message) {
		String notification = String.format("%s denied your file \"%s\"",
				getUser().getUsername(), myFile.getName());
		if (message == null || message.isEmpty()) {
			notification += ".";
		} else {
			notification +=  " with the message \"" + message + "\"";
		}
		awaitingReply = false;
		getUser().fireUserNotificationEvent(notification);
		clean();
	}

	@Override
	protected void timeOut() {
		String msg = String.format("Your request to send file \"%s\" to user "
				+ "%s timed out.", myFile.getName(), getUser().getUsername());
		getUser().fireUserNotificationEvent(msg);
		clean();
	}

	@Override
	/**
	 * Silently closes the request. If we are currently transferring a file,
	 * the transfer process will be aborted.
	 */
	protected void kill() {
		if (mySender != null) {
			mySender.abort();
		}
		clean();
	}
	
	/**
	 * Performs cleanup work when this request should be removed.
	 */
	private void clean() {
		awaitingReply = false;
		disableTimeOut();
		getUser().myRequests.remove(this);
	}

	@Override
	public void incrementPercentProgress(int currentPercent) {
		/* Do nothing */
	}

	@Override
	public void progressTerminatedDueToError(Exception e) {
		clean();
	}

	@Override
	public void processFinished() {
		clean();
	}
	
	/**
	 * This exception is thrown if an attempt is made to call accept() before
	 * a port number was specified using setPort().
	 * @author thaggus
	 *
	 */
	public static class NoPortSpecifiedException extends NullPointerException {
		
		public NoPortSpecifiedException() {
			super("A port number to connect to must be specified via setPort()"
					+ " before calling accept().");
		}
	}
}
