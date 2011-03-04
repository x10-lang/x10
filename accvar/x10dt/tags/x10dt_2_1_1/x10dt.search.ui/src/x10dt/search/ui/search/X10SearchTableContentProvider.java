package x10dt.search.ui.search;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Table;

import org.eclipse.jface.viewers.TableViewer;

import org.eclipse.search.ui.text.AbstractTextSearchResult;

public class X10SearchTableContentProvider extends X10SearchContentProvider {
	public X10SearchTableContentProvider(X10SearchResultPage page) {
		super(page);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof AbstractTextSearchResult) {
			Set filteredElements= new HashSet();
			Object[] rawElements= ((AbstractTextSearchResult)inputElement).getElements();
			int limit= getPage().getElementLimit().intValue();
			for (int i= 0; i < rawElements.length; i++) {
				if (getPage().getDisplayedMatchCount(rawElements[i]) > 0) {
					filteredElements.add(rawElements[i]);
					if (limit != -1 && limit < filteredElements.size()) {
						break;
					}
				}
			}
			return filteredElements.toArray();
		}
		return EMPTY_ARR;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void elementsChanged(Object[] updatedElements) {
		if (getSearchResult() == null)
			return;

		int addCount= 0;
		int removeCount= 0;
		int addLimit= getAddLimit();

		TableViewer viewer= (TableViewer) getPage().getViewer();
		Set updated= new HashSet();
		Set added= new HashSet();
		Set removed= new HashSet();
		for (int i= 0; i < updatedElements.length; i++) {
			if (getPage().getDisplayedMatchCount(updatedElements[i]) > 0) {
				if (viewer.testFindItem(updatedElements[i]) != null)
					updated.add(updatedElements[i]);
				else {
					if (addLimit > 0) {
						added.add(updatedElements[i]);
						addLimit--;
						addCount++;
					}
				}
			} else {
				removed.add(updatedElements[i]);
				removeCount++;
			}
		}

		viewer.add(added.toArray());
		viewer.remove(removed.toArray());
	}

	private int getAddLimit() {
		int limit= getPage().getElementLimit().intValue();
		if (limit != -1) {
			Table table= (Table) getPage().getViewer().getControl();
			int itemCount= table.getItemCount();
			if (itemCount >= limit) {
				return 0;
			}
			return limit - itemCount;
		}
		return Integer.MAX_VALUE;
	}

	public void clear() {
		getPage().getViewer().refresh();
	}

}

