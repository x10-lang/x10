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
	 * Simple enum to represent the number of arguments a Kind
	 * can have. MANY means 1 or more (this is mainly for debugging
	 * purposes).
	 * @author lshadare
	 *
	 */
	protected enum Arity {
		ONE,
		TWO,
		ANY
	}

	/**
	 * An XKind represents the kind of the operator.  
	 * @author lshadare
	 *
	 */
	public enum Kind {
		APPLY_LABEL("", "", Arity.ANY), // apply a label to the arguments (this had arity MANY instead of ONE to handle tuples)
		APPLY("", "", Arity.ANY), // apply a function to its arguments
		EQ("==", "=", Arity.TWO),	
		NOT("!", "not", Arity.ONE),
		AND("&&", "and", Arity.ANY),
		OR("||", "or", Arity.ANY),
		IMPL("=>", "=>", Arity.TWO),
		NEQ("!=", "!=", Arity.TWO);

		String prettyName; 
		String smt2; // the smt2 representation of the kind
		Arity arity; // the number of arguments this Kind permits

		Kind(String pn, String smt2, Arity arity) {
			this.prettyName = pn; 
			this.smt2 = smt2; 
			this.arity = arity; 
		}
		String prettyPrint() {
			return prettyName; 
		}
		String print(XPrinter<? extends XType> p) {
			if (p instanceof XSmtPrinter) {
				return smt2;
			}
			throw new IllegalArgumentException("Unsupported printer " + p);
		}

		Arity arity() {
			return arity; 
		}
	}
	
	/**
	 * The kind of this operator. 
	 */
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
	public abstract T type(); 

	/**
	 * Some useful operators. 
	 */
	public static <T extends XType> XSimpleOp<T> EQ(T type) {
		return new XSimpleOp<T>(Kind.EQ, type);
	}
	public static <T extends XType> XOp<T> NEQ(T type) {
		return new XSimpleOp<T>(Kind.NEQ, type);
	}
	public static <T extends XType> XSimpleOp<T> NOT(T type) {
		return new XSimpleOp<T>(Kind.NOT, type);
	}
	public static <T extends XType> XSimpleOp<T> AND(T type) {
		return new XSimpleOp<T>(Kind.AND, type);
	}
	public static <T extends XType> XSimpleOp<T> OR(T type) {
		return new XSimpleOp<T>(Kind.OR, type);
	}
	public static <T extends XType> XSimpleOp<T> IMPL(T type) {
		return new XSimpleOp<T>(Kind.IMPL, type);
	}
	public static <T extends XType> XSimpleOp<T> APPLY(T type) {
		return new XSimpleOp<T>(Kind.APPLY, type);
	}

	/**
	 * Constructs a labeled operator that represents the member (field/method) dereference 
	 * corresponding to the definition. 
	 * 
	 * @param def definition of member
	 * @param type the type of the member 
	 * @return 
	 */
	public static <T extends XType, D extends XDef<T>> XLabeledOp<T,D> makeLabelOp(D def) {
		return new XLabeledOp<T,D>(def, def.resultType());
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
	/**
	 * Return a string representation of the operator that is
	 * valid for the given printer. 
	 * @param p 
	 * @return 
	 */
	public abstract String print(XPrinter<T> p);
	public abstract String toString();
	public abstract String prettyPrint();	

	/**
	 * Check that the expression can have n children
	 * @param n arity to be checked
	 * @return
	 */
	public boolean isArityValid(int n) {
		if (n < 0)
			throw new IllegalArgumentException("Illegal arity.");
		Arity a = getKind().arity(); 

		if (a == Arity.ONE)
			return n == 1; 

		if (a == Arity.TWO)
			return n == 2;

		assert a == Arity.ANY;
		return true; 
	}
	/**
	 * Return the polyglot Expr operator corresponding to this operator. 
	 * @return
	 */
	public String asExprOperator() {
		assert kind != Kind.APPLY && kind != Kind.APPLY_LABEL; 
		return kind.prettyPrint();
	}

}
