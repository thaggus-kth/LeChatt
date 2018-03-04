package file;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class for observers wishing to monitor a progressor whose start is delayed.
 * @author thaggus
 *
 */
public class ProgressorProxy extends Progressor {

	Progressor realProgressor = null;
	
	private ArrayList<ProgressObserver> myObservers =
			new ArrayList<ProgressObserver>();
	
	@Override
	public void addObserver(ProgressObserver o) {
		myObservers.add(o);
	}
	@Override
	public void removeObserver(ProgressObserver o) {
		myObservers.remove(o);
	}
	
	/**
	 * Adds all observers to the parameter Progressor's observers, and
	 * clears this object's list of observers (in an attempt to get
	 * garbage collected).
	 * @param p
	 */
	public void realProgressorAvailable(Progressor p) {
		Iterator<ProgressObserver> iterator = myObservers.iterator();
		while (iterator.hasNext()) {
			ProgressObserver o = iterator.next();
			p.addObserver(o);
			iterator.remove();
		}
		realProgressor = p;
	}
	
	@Override
	public void abort() {
		if (realProgressor != null) {
			realProgressor.abort();
		} else {
			for (ProgressObserver o : myObservers) {
				o.progressTerminatedDueToError(null);
			}
		}
	}
}
