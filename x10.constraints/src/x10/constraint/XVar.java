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

import java.util.List;



public abstract class XVar extends XTerm  {
	/** In case this is a field selection x.f1...fn, return x, x.f1, x.f1.f2, ... x.f1.f2...fn */
	abstract XVar[] vars();

	/** In case this is a field selection x.f1...fn, return x, else this. */
	abstract XVar rootVar();

	public XVar() {
		super();
	}

	public XTerm subst(XTerm y, XVar x, boolean propagate) {
		return equals(x) ? y : super.subst(y, x, propagate);
	}

	@Override
	public XPromise internIntoConstraint(XConstraint c, XPromise last) throws XFailure {
		XVar[] vars = vars();
		XVar baseVar = vars[0];
		XPromise p = c.internBaseVar(baseVar, vars.length == 1, last);
		return p.intern(vars, 1, last);
	}
	
}

