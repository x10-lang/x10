/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.constraint.xnative;


/**
 * Tagging formula for != constraints.
 * Not treated as an atomic formula.
 * @author vj
 *
 */

public class XDisEquals extends XNativeFormula<String> implements x10.constraint.XDisEquals {
    private static final long serialVersionUID = 1513527157734278225L;
    public static final String name = "!==";
    public static final String asExprName = "!=";
    
    public XDisEquals(XNativeTerm left, XNativeTerm right) {
		super(name, asExprName, false, left, right);
	}
    @Override
	public XPromise internIntoConstraint(XNativeConstraint c, XPromise last)  {
	    XPromise p = c.intern((XNativeTerm)left());
	    if (p == null) return null;
	    XPromise q = c.intern((XNativeTerm)right());
	    if (q == null) return null;
	    
	    XNativeTerm pTerm = p.term(), qTerm = q.term();
	    if (pTerm instanceof XNativeLit && qTerm instanceof XNativeLit) {
	            return c.intern((XNativeTerm) (pTerm.equals(qTerm)?XNativeLit.FALSE:XNativeLit.TRUE));
	    }
	    else {
	        if (p != q || ! pTerm.equals(qTerm)) // Handle x==x also
	            return c.intern((XNativeTerm)XNativeLit.TRUE);
	        else return super.internIntoConstraint(c, last);
	    }
	}
	
	@Override 
	public String toString() {return left() + "!=" + right();}

}

