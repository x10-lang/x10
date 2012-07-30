package x10.constraint.smt;

import java.util.List;

import x10.constraint.XExpr;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XType;

public abstract class XSmtTerm<T extends XType> implements XTerm<T> {
	T type; 

	public XSmtTerm(T t) {
		assert t!= null; 
		this.type = t; 
	}

	public XSmtTerm(XSmtTerm<T> t) {
		assert t!= null;
		this.type = t.type;
	}
	
	@Override
	public T type() {
		assert type != null; 
		return type; 
	}
	
	/**
	 * Default implementation of substitution of XTerms. 
	 */
	@Override
	public XSmtTerm<T> subst(XTerm<T> t1, XTerm<T> t2) {
		if (this.equals(t1))
			return (XSmtTerm<T>)t2;
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
	public boolean okAsNestedTerm() {
		return true;
	}
	
	@Override
	public XSmtTerm<T> accept(TermVisitor<T> visitor) {
        XSmtTerm<T> res = (XSmtTerm<T>)visitor.visit(this);
        if (res!=null) return res;
        return this;
	}

	@Override
	public String prettyPrint() {
		return toString(); 
	}
	
	@Override
	public List<XTerm<T>> vars() {
		throw new UnsupportedOperationException("Need to add this");
	}
	@Override
	public boolean isEquals() {
		if (this instanceof XExpr) {
			@SuppressWarnings("unchecked")
			XExpr<T> exp = (XExpr<T>) this; 
			if (exp.op() == XOp.EQ())
				return true; 
		}
		return false; 
	} 
	@Override
	public boolean isAnd() {
		if (this instanceof XExpr) {
			@SuppressWarnings("unchecked")
			XExpr<T> exp = (XExpr<T>) this; 
			if (exp.op() == XOp.AND())
				return true; 
		}
		return false; 
	} 

	public abstract void print(XPrinter<T> p);
	public abstract boolean equals(Object o);
	public abstract int hashCode();
	public abstract String toString();
	
}
