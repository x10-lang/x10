package x10.errors;

import polyglot.util.ErrorInfo;
import polyglot.util.Position;

public class X10ErrorInfo extends ErrorInfo {
	public static final int INVARIANT_VIOLATION_KIND = 8;

	public static final String INVARIANT_VIOLATION_STRING = "AST invariant violation";

	public X10ErrorInfo(int kind, String message, Position position) {
		super(kind, message, position);
	}

	public String getErrorString() {
		if (kind == INVARIANT_VIOLATION_KIND) {
			return INVARIANT_VIOLATION_STRING;
		}
		return getErrorString(kind);
	}
}
