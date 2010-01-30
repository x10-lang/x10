/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

public class XNot_c extends XFormula_c implements XNot {
	
	public XNot_c(XTerm arg) {
		super(XTerms.notName, arg);
	}
	
	public XPromise internIntoConstraint(XConstraint_c c, XPromise last) throws XFailure {
		assert false : "Should not intern " + this;
		return super.internIntoConstraint(c, last);
	}
	
	@Override
	public String toString() {
		return "!" + unaryArg();
	}
}
