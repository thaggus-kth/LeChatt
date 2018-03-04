package file;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Performs some action which has a progress measured in percent completeness
 * that we wish to monitor, e.g. a file transfer. This class together with
 * ProgressObserver provides the neccessary framework to keep observers updated
 * on the progress. Subclasses should implement the action whose progress is
 * to be measured.
 * @author thaggus
 * @see {@link ProgressObserver}
 *
 */
public abstract class Progressor {
	
	private ArrayList<ProgressObserver> myObservers =
			new ArrayList<ProgressObserver>();
	
	public void addObserver(ProgressObserver o) {
		myObservers.add(o);
	}
	
	public void removeObserver(ProgressObserver o) {
		myObservers.remove(o);
	}
	
	/**
	 * Aborts the process. Useful for terminating the process on user's action,
	 * e.g. user cancels process or closes session which process is part of.
	 */
	public abstract void abort();
	
	/**
	 * Notifies the observers that we have progressed a certain percent.
	 * When the process starts, this should be called with a value of 0.
	 * @param percentProgress percent progress as integer between 0 and 100.
	 */
	protected void updateObserversOnProgress(int percentProgress) {
		for (ProgressObserver o : myObservers) {
			o.incrementPercentProgress(percentProgress);
		}
	}
	
	/**
	 * Call this method if there was an exception which caused the process
	 * to terminate. Observers will try to display the information in the
	 * exception to the user.
	 * @param e The cause of termination.
	 */
	protected void signalErrorToObservers(Exception e) {
		for (ProgressObserver o : myObservers) {
			o.progressTerminatedDueToError(e);
		}
	}
	
	/**
	 * Call this method to signal normal (i.e. error-free completion of the
	 * process to the observers.
	 */
	protected void signalNormalTermination() {
		for (ProgressObserver o : myObservers) {
			o.processFinished();
		}
	}
}
