/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.constraint.xnative;

/**
   Class representing left == right constraints.
   Not treated as an atomic formula.
   @author njnystrom
 */
public class XEquals extends XNativeFormula<String> implements x10.constraint.XEquals {
    private static final long serialVersionUID = 5003576451176083570L;
    public static final String name = "===";
	public static final String asExprName = "==";
    
	public XEquals(XNativeTerm left, XNativeTerm right) {
		super(name, asExprName, false, left, right);
	}
	@Override
	public XPromise internIntoConstraint(XNativeConstraint c, XPromise last)  {
	    XPromise p = c.intern((XNativeTerm)left());
	    if (p == null) return null;
	    XPromise q = c.intern((XNativeTerm)right());
	    if (q == null) return null;

	    XNativeTerm pTerm=p.term(),qTerm=q.term();
	    if (pTerm instanceof XNativeLit && qTerm instanceof XNativeLit) {
	            return c.intern((XNativeTerm) (p.equals(q) ? XNativeLit.TRUE : XNativeLit.FALSE));
	    }
	    else {
	        if (p == q || p.term().equals(q.term())) // Handle x==x also
	            return c.intern((XNativeTerm) XNativeLit.TRUE);
	        else return super.internIntoConstraint(c, last);
	    }
	}
	
	@Override 
	public String toString() {return left() + "==" + right();}
}

