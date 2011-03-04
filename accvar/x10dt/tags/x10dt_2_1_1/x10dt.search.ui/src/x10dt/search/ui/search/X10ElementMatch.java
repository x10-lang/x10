package x10dt.search.ui.search;

import org.eclipse.search.ui.text.Match;

/**
 *  A search match for X10 elements.
 * @author mvaziri
 *
 */
public class X10ElementMatch extends Match {
	private final boolean fIsWriteAccess;
	private final boolean fIsReadAccess;

	X10ElementMatch(Object element, int offset, int length, boolean isReadAccess, boolean isWriteAccess) {
		super(element, offset, length);
		fIsWriteAccess= isWriteAccess;
		fIsReadAccess= isReadAccess;
	}

	public boolean isWriteAccess() {
		return fIsWriteAccess;
	}

	public boolean isReadAccess() {
		return fIsReadAccess;
	}

}
