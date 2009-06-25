/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

public class XEquals_c extends XFormula_c implements XEquals {
	
	public XEquals_c(XTerm left, XTerm right) {
		super(XTerms.equalsName, left, right);
	}
	
	public XPromise internIntoConstraint(XConstraint c, XPromise last) throws XFailure {
		assert false : "Should not intern " + this;
		return super.internIntoConstraint(c, last);
	}
	
	@Override
	public String toString() {
		return left() + "==" + right();
	}

}
