package x10.constraint;
/**
 * Representation of an expression operator for XExpr occurring in constraints. 
 * 
 * @author lshadare
 *
 */
public abstract class XOp<T extends XType> {
	/**
	 * An XKind represents the kind of the operator.  
	 * @author lshadare
	 *
	 */
	public enum Kind {
		APPLY("apply"), 
		TAG("tag"),
		EQ("="),	
		NOT("not"),
		AND("and"),
		OR("or"),
		IMPL("=>");
		String name;
		Kind(String n) {
			this.name = n;
		}
	}
	
	Kind kind; 
	
	XOp(Kind kind) {
		this.kind = kind; 
	}
	
	/**
	 * Returns the kind of the operator. 
	 * @return
	 */
	public Kind getKind() {
		return kind; 
	}
	
	/**
	 * Returns the type of the term resulting from applying this operator
	 * e.g. for EQ the returned type would be Boolean. 
	 * @return
	 */
	public abstract T type(XTypeSystem<? extends T> ts); 
	
	public abstract T type(); 
	
	/**
	 * Some useful operators. 
	 */

	public static <T extends XType> XSimpleOp<T> EQ() {
		return new XSimpleOp<T>(Kind.EQ);
	}
	public static <T extends XType> XSimpleOp<T> NOT() {
		return new XSimpleOp<T>(Kind.NOT);
	}
	public static <T extends XType> XSimpleOp<T> AND() {
		return new XSimpleOp<T>(Kind.AND);
	}
	public static <T extends XType> XSimpleOp<T> OR() {
		return new XSimpleOp<T>(Kind.OR);
	}
	public static <T extends XType> XSimpleOp<T> IMPL() {
		return new XSimpleOp<T>(Kind.IMPL);
	}
	public static <T extends XType, D extends XDef<T>> XLabeledOp<T, D> APPLY(D def) {
		return new XLabeledOp<T,D>(def);
	}
	public static <T extends XType, D> XTagOp<T, D> TAG(D def, T type) {
		return new XTagOp<T,D>(def, type);
	}

	
	/**
	 * Constructs a labeled operator that represents the application 
	 * of the field/method corresponding to the field/method definition
	 * def. 
	 * 
	 * @param def
	 * @return 
	 */
	public static <T extends XType, D extends XDef<T>> XLabeledOp<T,D> makeLabelOp(D def) {
		return new XLabeledOp<T,D>(def);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XOp<?> other = (XOp<?>) obj;
		if (kind != other.kind)
			return false;
		return true;
	}

	public abstract String toString();

	public Object asExprOperator() {
		// TODO Auto-generated method stub
		return null;
	} 
	
}
