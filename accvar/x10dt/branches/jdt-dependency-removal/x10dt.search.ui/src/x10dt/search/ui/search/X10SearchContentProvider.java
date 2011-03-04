package x10dt.search.ui.search;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.search.ui.text.AbstractTextSearchResult;


public abstract class X10SearchContentProvider implements IStructuredContentProvider {
	protected final Object[] EMPTY_ARR= new Object[0];

	private AbstractTextSearchResult fResult;
	private X10SearchResultPage fPage;

	X10SearchContentProvider(X10SearchResultPage page) {
		fPage= page;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		initialize((AbstractTextSearchResult) newInput);

	}

	protected void initialize(AbstractTextSearchResult result) {
		fResult= result;
	}

	public abstract void elementsChanged(Object[] updatedElements);
	public abstract void clear();

	public void dispose() {
		// nothing to do
	}

	X10SearchResultPage getPage() {
		return fPage;
	}

	AbstractTextSearchResult getSearchResult() {
		return fResult;
	}

}
