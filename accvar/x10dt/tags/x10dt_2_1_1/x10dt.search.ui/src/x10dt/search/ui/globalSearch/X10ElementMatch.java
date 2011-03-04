package x10dt.search.ui.globalSearch;

import org.eclipse.search.ui.text.Match;

/**
 * A search match with additional java-specific info.
 */
public class X10ElementMatch extends Match {
	private final int fAccuracy;
	private final int fMatchRule;
	private final boolean fIsWriteAccess;
	private final boolean fIsReadAccess;
	private final boolean fIsJavadoc;
	private final boolean fIsSuperInvocation;

	X10ElementMatch(Object element, int matchRule, int offset, int length, int accuracy, boolean isReadAccess, boolean isWriteAccess, boolean isJavadoc, boolean isSuperInvocation) {
		super(element, offset, length);
		fAccuracy= accuracy;
		fMatchRule= matchRule;
		fIsWriteAccess= isWriteAccess;
		fIsReadAccess= isReadAccess;
		fIsJavadoc= isJavadoc;
		fIsSuperInvocation= isSuperInvocation;
	}

	public int getAccuracy() {
		return fAccuracy;
	}

	public boolean isWriteAccess() {
		return fIsWriteAccess;
	}

	public boolean isReadAccess() {
		return fIsReadAccess;
	}

	public boolean isJavadoc() {
		return fIsJavadoc;
	}

	public boolean isSuperInvocation() {
		return fIsSuperInvocation;
	}

	public int getMatchRule() {
		return fMatchRule;
	}
}
