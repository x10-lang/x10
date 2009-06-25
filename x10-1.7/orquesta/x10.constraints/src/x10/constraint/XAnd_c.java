/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Language.
 *
 */     
package x10.constraint;

import java.util.Collections;
import java.util.HashMap;

public class XAnd_c extends XFormula_c implements XAnd {
	
	public XAnd_c(XTerm left, XTerm right) {
		super(XTerms.andName, left, right);
	}
	
	public XPromise internIntoConstraint(XConstraint c, XPromise last) throws XFailure {
		assert false : "Should not intern " + this;
		return super.internIntoConstraint(c, last);
	}

	@Override
	public String toString() {
		return left() + ", " + right();
	}

}
