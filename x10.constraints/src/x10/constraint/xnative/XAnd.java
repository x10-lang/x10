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

import x10.constraint.xnative.XPromise;

/**
 * Represents the conjunction of two formulas.
 * (Not treated as an atomic formula.)
 * @author njnystrom
 * @author vj
 *
 */

public class XAnd extends XNativeFormula<String> implements x10.constraint.XAnd {
    private static final long serialVersionUID = 6884928193006059913L;
    public static final String name = "&&&";
    public static final String asExprName = "&&";
    
    public XAnd(XNativeTerm left, XNativeTerm right) {
    	super(name, asExprName, false, left, right);
    }
    @Override
	public XPromise internIntoConstraint(XNativeConstraint c, XPromise last) {
	    return super.internIntoConstraint(c, last);
	}
	@Override
	public String toString() {return left() + "&&" + right();}
}
