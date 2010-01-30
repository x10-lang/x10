/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Language.
 *
 */     
package x10.constraint;


public class XAnd_c extends XFormula_c implements XAnd {
	
	public XAnd_c(XTerm left, XTerm right) {
		super(XTerms.andName, left, right);
	}
	
	public XPromise internIntoConstraint(XConstraint_c c, XPromise last) throws XFailure {
		return super.internIntoConstraint(c, last);
	}

	@Override
	public String toString() {
		return left() + ", " + right();
	}

}
