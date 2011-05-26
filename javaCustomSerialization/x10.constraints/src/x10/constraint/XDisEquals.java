/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.constraint;

/**
 * Tagging formula for != constraints.
 * Not treated as an atomic formula.
 * @author vj
 *
 */


public class XDisEquals extends XFormula<String> {


	public XDisEquals(XTerm left, XTerm right) {
		super(XTerms.disEqualsName, XTerms.asExprDisEqualsName, false, left, right);
	}
	public XPromise internIntoConstraint(XConstraint c, XPromise last)  {
	    XPromise p = c.intern(left());
	    if (p == null)
	        return null;
	    XPromise q = c.intern(right());
	    if (q == null)
	        return null;

	    XTerm pTerm = p.term(), qTerm = q.term();
	    if (pTerm instanceof XLit && qTerm instanceof XLit) {
	        if (pTerm.equals(qTerm))
	            return c.intern(XTerms.FALSE);
	        else
	            return c.intern(XTerms.TRUE);
	    }
	    else {
	        if (p != q || ! pTerm.equals(qTerm))
	            // Handle x==x also
	            return c.intern(XTerms.TRUE);
	        else
	            return super.internIntoConstraint(c, last);
	    }
	}
	
	@Override
	public String toString() {
		return left() + "!=" + right();
	}

}

