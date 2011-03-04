package x10dt.search.ui.globalSearch;

import org.eclipse.jface.action.Action;


public class GroupAction extends Action {
	private int fGrouping;
	private X10SearchResultPage fPage;

	public GroupAction(String label, String tooltip, X10SearchResultPage page, int grouping) {
		super(label);
		setToolTipText(tooltip);
		fPage= page;
		fGrouping= grouping;
	}

	public void run() {
		fPage.setGrouping(fGrouping);
	}

	public int getGrouping() {
		return fGrouping;
	}
}
