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

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class XTerm_c implements XTerm, Cloneable {
	public XTerm_c() {
		super();
	}

	// The default is OBJECT. May be overridden by subclasses.
	public XTermKind kind() { return XTermKind.OBJECT;}
	
	public Solver solver() {
		return null;
	}
	
	public final XTerm subst(XTerm y, XRoot x) {
	    return subst(y, x, true);
	}

	int nextId = 0;
	
	public XTerm subst(XTerm y, final XRoot x, boolean propagate) {
	    XTerm_c t = this;
	    return t;
	}

	@Override
	public XTerm_c clone() {
		try {
			XTerm_c n = (XTerm_c) super.clone();
			return n;
		}
		catch (CloneNotSupportedException e) {
			return this;
		}
	}

	public boolean rootVarIsSelf() {
		return false;
	}

	public boolean hasEQV() {
		return false;
	}
	public boolean isEQV() {
		return false;
	}

	public boolean prefixes(XTerm term) {
		return false;
	}

	public boolean prefersBeingBound() {
		return toString().startsWith("_self") || hasEQV();
	}

	protected boolean isAtomicFormula = false;

	public boolean isAtomicFormula() {
		return isAtomicFormula;
	}

	public void markAsAtomicFormula() {
		isAtomicFormula = true;
	}
}
