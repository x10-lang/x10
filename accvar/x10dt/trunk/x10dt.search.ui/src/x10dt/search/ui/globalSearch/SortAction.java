package x10dt.search.ui.globalSearch;

import org.eclipse.swt.custom.BusyIndicator;

import org.eclipse.jface.action.Action;


public class SortAction extends Action {
	private int fSortOrder;
	private X10SearchResultPage fPage;

	public SortAction(String label, X10SearchResultPage page, int sortOrder) {
		super(label);
		fPage= page;
		fSortOrder= sortOrder;
	}

	public void run() {
		BusyIndicator.showWhile(fPage.getViewer().getControl().getDisplay(), new Runnable() {
			public void run() {
				fPage.setSortOrder(fSortOrder);
			}
		});
	}

	public int getSortOrder() {
		return fSortOrder;
	}
}
