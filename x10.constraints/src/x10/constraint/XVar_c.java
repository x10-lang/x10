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

public abstract class XVar_c extends XTerm_c implements XVar {
	public XVar_c() {
		super();
	}

	public XTerm subst(XTerm y, XRoot x, boolean propagate) {
		return equals(x) ? y : super.subst(y, x, propagate);
	}

	public XPromise internIntoConstraint(XConstraint_c c, XPromise last) throws XFailure {
		XVar[] vars = vars();
		XVar baseVar = vars[0];
		XPromise p = c.internBaseVar(baseVar, vars.length == 1, last);
		return p.intern(vars, 1, last);
	}
}
