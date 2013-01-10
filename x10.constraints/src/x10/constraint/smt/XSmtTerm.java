package x10.constraint.smt;

import x10.constraint.XCommonTerm;
import x10.constraint.XConstraintSystem;
import x10.constraint.XTerm;
import x10.constraint.XType;

public abstract class XSmtTerm<T extends XType> extends XCommonTerm<T> implements XTerm<T> {

	public XSmtTerm(T t) {
		super(t);
	}

	public XSmtTerm(XSmtTerm<T> t) {
		this(t.type());
	}

	@Override
	public XSmtTerm<T> accept(TermVisitor<T> v) {
		return (XSmtTerm<T>) super.accept(v);
	}

	/**
	 * Default implementation of substitution of XTerms. 
	 */
	@Override
	public XSmtTerm<T> subst(XConstraintSystem<T> sys, XTerm<T> t1, XTerm<T> t2) {
		if (this.equals(t2))
			return (XSmtTerm<T>)t1;
		return this; 
	}

	/**
	 * Should be overridden by XTerms that are projections (i.e. variables, field/method
	 * dereferencing 
	 */
	@Override
	public boolean isProjection() {
		return false;
	}

	/**
	 * In XSmtTerms we allow for all expressions in the constraints. 
	 */
	@Override
	public final boolean okAsNestedTerm() {
		return true;
	}
	

	protected abstract void print(XSmtConstraintSystem<T> cs, XPrinter<T> p);
	protected abstract void declare(XSmtConstraintSystem<T> cs, XPrinter<T> p);
	public abstract boolean equals(Object o);
	public abstract int hashCode();
	public abstract String toString();
	public abstract String toSmtString();
	
	public abstract XSmtTerm<T> copy();
	
}
