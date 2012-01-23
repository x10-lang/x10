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
   Class representing left == right constraints.
   Not treated as an atomic formula.
   @author njnystrom
 */
public class XEquals extends XFormula<String>  {
    private static final long serialVersionUID = 5003576451176083570L;

    public XEquals(XTerm left, XTerm right) {
		super(XTerms.equalsName, XTerms.asExprEqualsName, false, left, right);
	}
	
	public XPromise internIntoConstraint(XConstraint c, XPromise last)  {
	    XPromise p = c.intern(left());
	    if (p == null) return null;
	    XPromise q = c.intern(right());
	    if (q == null) return null;

	    XTerm pTerm=p.term(),qTerm=q.term();
	    if (pTerm instanceof XLit && qTerm instanceof XLit) {
	            return c.intern(p.equals(q) ? XTerms.TRUE : XTerms.FALSE);
	    }
	    else {
	        if (p == q || p.term().equals(q.term())) // Handle x==x also
	            return c.intern(XTerms.TRUE);
	        else return super.internIntoConstraint(c, last);
	    }
	}
	
	@Override public String toString() {return left() + "==" + right();}
}

