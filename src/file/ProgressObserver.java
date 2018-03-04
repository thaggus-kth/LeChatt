package file;

/**
 * Interface for objects which wish to be updated by a Progressor's progress.
 * @author thaggus
 *
 */
public interface ProgressObserver {
	
	/**
	 * This method is called each time the progress increases by one-percent-
	 * unit. The start of the process is indicated by this method being called
	 * with 0 percent progress.
	 * @param currentPercent
	 */
	public void incrementPercentProgress(int currentPercent);
	
	/**
	 * This method is called when the process was terminated due to
	 * some error.
	 * @param e Exception which is the cause of the error, for information
	 * to user-purposes.
	 */
	public void progressTerminatedDueToError(Exception e);
	
	/**
	 * This method is called when the progress terminated normally (i.e. without
	 * error.
	 */
	public void processFinished();
}
