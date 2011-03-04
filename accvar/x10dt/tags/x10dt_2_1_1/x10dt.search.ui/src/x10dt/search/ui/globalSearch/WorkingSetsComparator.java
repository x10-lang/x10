package x10dt.search.ui.globalSearch;

import java.util.Comparator;

import com.ibm.icu.text.Collator;

import org.eclipse.ui.IWorkingSet;

class WorkingSetsComparator implements Comparator {

	private Collator fCollator= Collator.getInstance();

	/*
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(Object o1, Object o2) {
		String name1= null;
		String name2= null;

		if (o1 instanceof IWorkingSet[]) {
			IWorkingSet[] workingSets= (IWorkingSet[])o1;
			if (workingSets.length > 0)
				name1= workingSets[0].getLabel();
		}

		if (o2 instanceof IWorkingSet[]) {
			IWorkingSet[] workingSets= (IWorkingSet[])o1;
			if (workingSets.length > 0)
				name2= workingSets[0].getLabel();
		}

		return fCollator.compare(name1, name2);
	}
}