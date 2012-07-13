package x10.constraint.xsmt;

import x10.constraint.XTerm;
import x10.constraint.XVar;

public abstract class XSmtTerm implements XTerm, SmtTerm {
	private static final long serialVersionUID = -1545233587931598397L;
	
	
	/**
	 * In the Smt Constraint system we allow arbitrarily nested terms. 
	 */
	@Override
	public final boolean okAsNestedTerm() {
		return true;
	}

	@Override
	public boolean isLit() {
		return false; 
	}

	@Override
	public boolean isSelf() {
		return false;
	}

	@Override
	public boolean isThis() {
		return false;
	}

	@Override
	public boolean isField() {
		return false;
	}

	@Override
	public boolean isBoolean() {
		return false;
	}
	
	@Override
	public abstract XSmtTerm subst(XTerm x, XVar v);
	
	@Override
	public XSmtTerm clone() {
		try {
			XSmtTerm n = (XSmtTerm) super.clone();
			return n;
		}
		catch (CloneNotSupportedException e) {
			return this;
		}
	}
	
	/**
	 * The default implementation for leaf terms that do not have any children. 
	 */
	@Override
    public XSmtTerm accept(TermVisitor visitor) {
        // The default implementation for "leaf" terms (that do not have any children)
        XSmtTerm res = (XSmtTerm)visitor.visit(this);
        if (res!=null) return res;
        return this;
    }
    
	@Override
	public SmtType getType() {
		return SmtType.USort();
	}
	
	/**
	 * Sets the type of the XSmtTerm. Not all XSmtTerms need to
	 * implement this, this is why the default is empty. 
	 * @param t
	 */
	public void setType(SmtType t) {}
	
	public abstract boolean equals(Object x);
	public abstract String toString();
	public abstract int hashCode();
}
