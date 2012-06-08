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
import x10.util.CollectionFactory;

/**
 * A representation of a literal. A literal is an XVar carrying a
 * payload that is equal to any other XLit carrying the same payload.
 * 
 * This class and its subclasses should not have mutable state.
 * @author vijay
 *
 */
public class XLit extends XVar  {
    private static final long serialVersionUID = 566522638402563631L;
    final protected Object val;

	public XLit(Object l) {val = l;}
	public Object val() {return val;}
	/**
	 * Pro-actively intern literals since they may end up having fields.
	 */
	public XPromise nfp(XNativeConstraint c) {
		assert c != null;
		XPromise p = null;
		if (c.roots == null) {
			c.roots = CollectionFactory.<XTerm, XPromise> newHashMap();
			p = c.intern(this);
		} else {
			p = c.roots.get(this);
			if (p == null) p = c.intern(this);
		}
		return p.lookup();
	}
	// public boolean hasDisBindings() { return false; }

	public XTermKind kind()  {return XTermKind.LITERAL;}
	public List<XEQV> eqvs() {return Collections.emptyList();}

	@Override
	public int prefersBeingBound() {return XTerm.TERM_MUST_NOT_BE_BOUND;}
	public String toString(String prefix) {return toString();}
	public String toString() {
	    if (val == null)              return "null";
	    if (val instanceof String)    return "\"" + val.toString() + "\"";
	    if (val instanceof Character) return "'" + val.toString() + "'";
	    if (val instanceof Float)     return val.toString() + "F";
	    if (val instanceof Long)      return val.toString() + "L";
	    return val.toString();
	}

	public int hashCode() {return ((val == null) ? 0 : val.hashCode());}
	public boolean okAsNestedTerm() { return true;}
	public boolean hasVar(XVar v) { return v.equals(this);}

	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof XLit)) return false;
	    XLit other = (XLit) o;
	    return val == null ? o == null : val == other.val || val.equals(other.val);
	}

	@Override
	public XTerm subst(XTerm y, XVar x, boolean propagate) {
	    return super.subst(y, x, propagate);
	}
	public String instance() {return toString();}

	XVar[] vars;
	/** In case this is a field selection x.f1...fn, return x, x.f1, x.f1.f2, ... x.f1.f2...fn */
	public XVar[] vars() {
	    if (vars==null) vars = new XVar[]{this};
	    return vars;
	}

	/** In case this is a field selection x.f1...fn, return x, else this. */
	public XVar rootVar() {return this;}
}
