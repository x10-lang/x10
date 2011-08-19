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
 * A representation of !c. Place holder for propagation rules for !.
 * Not treated as an atomic formula.
 * 
 * @author vijay
 *
 */
public class XNot extends XFormula<String>  {
	
	public XNot(XTerm arg) {
		super(XTerms.notName, XTerms.asExprNotName, false, arg);
	}
	
	public XPromise internIntoConstraint(XConstraint c, XPromise last)  {
		assert false : "Should not intern " + this;
		return super.internIntoConstraint(c, last);
	}
	
	@Override
	public String toString() {
		return "!" + unaryArg();
	}
}
