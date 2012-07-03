package x10.constraint.xsmt;

public final class SmtUtil {
	/**
	 * The different kinds of XSmtTerms (not to be confused with types)
	 * @author lshadare
	 *
	 */
	public static enum XSmtKind {
		CONSTANT,
		APPLY_UF,
		VARIABLE,
		UF, 
		AND,
		OR,
		NOT,
		EQ
	}; 
	
	public static XSmtKind toXSmtKind(String op) {
		if (op.equals("==="))
			return XSmtKind.EQ;
		if (op.equals("&&&"))
			return XSmtKind.AND;
		if (op.equals("|||"))
			return XSmtKind.OR;
		throw new IllegalArgumentException("Unknown kind: " + op);
	}

	public static SmtType toSmtType(String string) {
		// TODO Auto-generated method stub
		return null;
	}


}
