package session;

import java.io.File;
import java.io.IOException;

import file.*;
import session.*;
import crypto.*;

public class IncomingFileRequest extends FileRequest implements ProgressObserver {

	private long fileSize;
	private String fileName;
	private FileReciever fileReciever = null;
	private File destination = null;
	private ProgressorProxy progressorProxy = new ProgressorProxy();
	private Progressor progressor = progressorProxy;
	
	
	public IncomingFileRequest(User u, String message, CryptoType ct,
			String incomingFileName, long incomingFileSize) {
		super(u, message, ct);
		fileSize = incomingFileSize;
		fileName = incomingFileName;
	}
	
	
	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * Specifies where the incoming file should be saved to.
	 * @param f the file destination
	 */
	public void setFileDestination(File fileDestination) {
		destination = fileDestination;
	}
	
	/**
	 * Grants access to the enclosed FileReciever object via the Progressor
	 * interface, for visualization by view. Note that calling this before
	 * any call to accept() will return null.
	 * @return Progessor representation of the FileReciever if the recieving 
	 * process has started, or a proxy otherwise.
	 */
	public Progressor getFileSender() {
		return progressor;
	}
	
	@Override
	/**
	 * Call this method to accept the incoming file. This will open a
	 * connection for the transfer and send a reply to the remote user.
	 * The IncomingFileRequest will stick around until the file transfer
	 * is complete.
	 * @param message - the user's reply message.
	 */
	public void accept(String message) {
		User myUser = getUser();
		String response;
		Crypto cryptoToUse = null;
		if (destination == null) {
			throw new NoDestinationSpecifiedException();
		}
		disableTimeOut();
		
		if (getCryptoType() != CryptoType.PLAIN) {
			cryptoToUse = myUser.myCryptos.get(getCryptoType());
		}
		try {
			fileReciever = new FileReciever(destination, cryptoToUse, fileSize);
			fileReciever.addObserver(this);
			progressorProxy.realProgressorAvailable(fileReciever);
			progressor = fileReciever;
			response = String.format("<fileresponse reply=\"yes\" "
					+ "port=\"%d\">",
					fileReciever.getPort());
			response += message + "</fileresponse>";
			myUser.writeLine(response);
		} catch (IOException e) {
			String errMsg = String.format("Could not start transfer of file %s"
					+ "from user %s: " + e.getMessage() + "\nPlease try again.",
					fileName, myUser.getUsername()); 
			myUser.fireUserNotificationEvent(errMsg);
			clean();
		}
		
	}

	@Override
	public void deny(String message) {
		getUser().writeLine("<fileresponse reply=\"no\">" + message
				+ "</fileresponse>");
		clean();
	}

	@Override
	protected void timeOut() {
		String msg = String.format("The request from %s to send the file "
				+ "\"%s\" timed out.", getUser().getUsername(), fileName);
		getUser().fireUserNotificationEvent(msg);
		clean();
	}

	@Override
	/**
	 * Silently closes the request. If we are currently recieving a file, the
	 * process will be aborted.
	 */
	protected void kill() {
		if (fileReciever != null) {
			fileReciever.abort();
		}
		clean();
	}
	
	private void clean() {
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
	 * This exception is thrown if an attempt is made to accept the request
	 * before specifying where the file should be saved.
	 * @author thaggus
	 *
	 */
	public class NoDestinationSpecifiedException extends NullPointerException {
		
		public NoDestinationSpecifiedException() {
			super("A destination path for the incoming file must be set via "
					+ "setDestination() before calling accept().");
		}
	}
}
