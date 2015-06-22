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

package x10.types.constraints.xnative;

import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeLit;
import x10.types.constraints.XTypeLit;
import x10.types.matcher.Subst;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;

/**
 * A subclass of XLit that represents a Type literal.
 * @author vijay
 *
 */

public class XNativeTypeLit extends XNativeLit implements XTypeLit {
	private static final long serialVersionUID = -1222245257474719757L;

	public XNativeTypeLit(Type l) {
		super(l);
	}
	@Override
	public Type type() {
		return (Type) val;
	}

	@Override
	public boolean hasVar(XVar v) {
		return Types.hasVar(type(), v);
	}

	@Override
	public XNativeTypeLit subst(XTerm y, XVar x, boolean propagate) {
		XNativeTypeLit n = (XNativeTypeLit) super.subst(y, x, propagate);
		Type newVal = n.type();
		try {
			newVal = Subst.subst(type(), y, x);
		} catch (SemanticException e) { }
		if (newVal == n.type())
			return n;
		return new XNativeTypeLit(newVal);
	}

}
