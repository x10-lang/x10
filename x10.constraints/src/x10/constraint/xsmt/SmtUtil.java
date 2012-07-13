package x10.constraint.xsmt;

public final class SmtUtil {
	/**
	 * The different kinds of XSmtTerms (not to be confused with types)
	 * @author lshadare
	 *
	 */
	public static enum XSmtKind {
		CONSTANT,
		APPLY_UF, // this should be parametrized by the function symbol 
		VARIABLE,
		UF, 
		AND,
		OR,
		NOT,
		IMPL,
		EQ,
		NEQ
	}; 
	
	public static XSmtKind toXSmtKind(String op) {
		if (op.equals("=="))
			return XSmtKind.EQ;
		if (op.equals("&&"))
			return XSmtKind.AND;
		if (op.equals("||"))
			return XSmtKind.OR;
		if (op.equals("!="))
			return XSmtKind.NEQ;
		if (op.equals("!"))
			return XSmtKind.NOT;
		if (op.equals("=>"))
			return XSmtKind.IMPL; 
		throw new IllegalArgumentException("Unknown kind: " + op);
	}
	
	public static String toSmt2(XSmtKind kind) {
		switch(kind) {
		case AND : return "and"; 
		case NOT : return "not";
		case EQ : return "="; 
		case OR : return "or";
		case IMPL: return "=>"; 
		default:
			throw new IllegalArgumentException("Unsupported kind " + kind);
		}
	}

	/**
	 * Converts the given String to an SMT friendly identifier name
	 * @param name 
	 * @return 
	 */
	public static String mangle(String name) {
		return "?" + name.replaceAll("[\\.\\(\\):#\\s]", "_");
	}
	
	public static <T> SmtFuncSymbol makeFunctionSymbol(T field) {
		// here we have no type information
		return new SmtFuncSymbol(mangle(field.toString()), SmtType.USort(), SmtType.USort()); 
	}

	// sometimes we can infer some of the types from the receiver
	public static <T> SmtFuncSymbol makeFunctionSymbol(T field, SmtBaseType t1, SmtBaseType t2) {
		return new SmtFuncSymbol(mangle(field.toString()), t1, t2); 
	}

}
