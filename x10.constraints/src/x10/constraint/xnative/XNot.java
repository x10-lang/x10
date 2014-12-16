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
 * A representation of !c. Place holder for propagation rules for !.
 * Not treated as an atomic formula.
 * 
 * @author vijay
 *
 */
public class XNot extends XNativeFormula<String> implements x10.constraint.XNot {
    private static final long serialVersionUID = -425560155692514682L;
    public static final String name = "!!!";
    public static final String asExprName = "!";
    
    public XNot(XNativeTerm arg) {
    	super(name, asExprName, false, arg);
    }
    @Override
	public XPromise internIntoConstraint(XNativeConstraint c, XPromise last)  {
		assert false : "Should not intern " + this;
		return super.internIntoConstraint(c, last);
	}
	
	@Override public String toString() {return "!" + unaryArg();}
}
