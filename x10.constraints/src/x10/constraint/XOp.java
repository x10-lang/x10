package x10.constraint;

import x10.constraint.smt.XPrinter;
import x10.constraint.smt.XSmtPrinter;

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
		APPLY_LABEL("apply_label", "", ""),
		APPLY("apply","", ""),
		EQ("=", "=", "="),	
		NOT("not", "!", "not"),
		AND("and", "&&", "and"),
		OR("or", "||", "or"),
		IMPL("=>", "=>", "impl");
		String name;
		String prettyName;
		String smt2;
		Kind(String n, String pn, String smt2) {
			this.name = n;
			this.prettyName = pn; 
			this.smt2 = smt2; 
		}
		String prettyPrint() {
			return prettyName; 
		}
		void print(XPrinter<? extends XType> p) {
			if (p instanceof XSmtPrinter) {
				p.append(smt2);
			}
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
	public static <T extends XType> XSimpleOp<T> APPLY() {
		return new XSimpleOp<T>(Kind.APPLY);
	}
	public static <T extends XType, D> XLabeledOp<T, D> APPLY(D def, T type) {
		return new XLabeledOp<T,D>(def, type);
	}

	
	/**
	 * Constructs a labeled operator that represents the application 
	 * of the field/method corresponding to the field/method definition
	 * def. 
	 * 
	 * @param def
	 * @return 
	 */
	public static <T extends XType, D> XLabeledOp<T,D> makeLabelOp(D def, T type) {
		return new XLabeledOp<T,D>(def, type);
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

	public abstract void print(XPrinter<T> p);
	public abstract String toString();
	public Object asExprOperator() {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract String prettyPrint();	
}
