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

import x10.constraint.XTerm;
import x10.constraint.XVar;


public abstract class XNativeVar extends XNativeTerm implements XVar  {
    private static final long serialVersionUID = 6641600303056126651L;
    /** In case this is a field selection x.f1...fn, return x, 
	 * x.f1, x.f1.f2, ... x.f1.f2...fn 
	 * */
    @Override
	public XNativeVar[] vars() {return new XNativeVar[] { this };}
	public XNativeVar() {super();}
	@Override
	public XTermKind kind() { return XTermKind.LOCAL;}
	
	@Override public XNativeTerm subst(XTerm y, XVar x, boolean propagate) {
		return equals(x) ? (XNativeTerm)y : super.subst(y, x, propagate);
	}
	@Override
	public String toString() {
		return "Native var !!"; 
	}
	
	@Override public boolean isAtomicFormula() {return false;}
	@Override public XPromise internIntoConstraint(XNativeConstraint c, XPromise last) {
		XNativeVar[] vars = vars();
		XNativeVar baseVar = vars[0];
		XPromise p = c.internBaseVar(baseVar, vars.length == 1, last);
		if (p == null) return null;
		return p.intern(vars, 1, last);
	}
	
}

