/* 
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

public abstract class XVar_c extends XTerm_c implements XVar {
	public XVar_c() {
		super();
	}

	public XTerm subst(XTerm y, XRoot x, boolean propagate) {
		return equals(x) ? y : super.subst(y, x, propagate);
	}

	public XPromise internIntoConstraint(XConstraint c, XPromise last) throws XFailure {
		XVar[] vars = vars();
		XVar baseVar = vars[0];
		XPromise p = c.internBaseVar(baseVar, vars.length == 1, last);
		return p.intern(vars, 1, last);
	}
}
