package x10.constraint.redesign.xsmt;

import x10.constraint.redesign.XTerm;

public abstract class XSmtTerm<T extends XSmtType> implements XTerm<T> {
	T type; 

	public XSmtTerm(T t) {
		assert t!= null; 
		this.type = t; 
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
	
	public abstract void print(XPrinter p);
}
