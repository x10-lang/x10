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

import java.util.Collections;
import java.util.List;

import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.util.CollectionFactory;

/**
 * A representation of a literal. A literal is an XVar carrying a
 * payload that is equal to any other XLit carrying the same payload.
 * 
 * This class and its subclasses should not have mutable state.
 * @author vijay
 *
 */
public class XNativeLit extends XNativeVar implements XLit  {
    private static final long serialVersionUID = 566522638402563631L;
    final protected Object val;
    
    public static final XNativeLit TRUE = new XNativeLit(true);
    public static final XNativeLit FALSE = new XNativeLit(false);
    public static final XNativeLit NULL = new XNativeLit(null);

	public XNativeLit(Object l) {val = l;}
	@Override
	public Object val() {return val;}
	/**
	 * Pro-actively intern literals since they may end up having fields.
	 */
	@Override
	public XPromise nfp(XNativeConstraint c) {
		assert c != null;
		XPromise p = null;
		if (c.roots == null) {
			c.roots = CollectionFactory.<XNativeTerm, XPromise> newHashMap();
			p = c.intern(this);
		} else {
			p = c.roots.get(this);
			if (p == null) p = c.intern(this);
		}
		return p.lookup();
	}
	// public boolean hasDisBindings() { return false; }

	@Override
	public XTermKind kind()  {return XTermKind.LITERAL;}
	@Override
	public List<XNativeEQV> eqvs() {return Collections.emptyList();}

	@Override
	public int prefersBeingBound() {return XNativeTerm.TERM_MUST_NOT_BE_BOUND;}
	
	public String toString(String prefix) {return toString();}
	@Override
	public String toString() {
	    if (val == null)              return "null";
	    if (val instanceof String)    return "\"" + val.toString() + "\"";
	    if (val instanceof Character) return "'" + val.toString() + "'";
	    if (val instanceof Float)     return val.toString() + "F";
	    if (val instanceof Long)      return val.toString() + "L";
	    return val.toString();
	}

	@Override
	public int hashCode() {return ((val == null) ? 0 : val.hashCode());}
	@Override
	public boolean okAsNestedTerm() { return true;}
	@Override
	public boolean hasVar(XVar v) { return v.equals(this);}

	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof XNativeLit)) return false;
	    XNativeLit other = (XNativeLit) o;
	    return val == null ? o == null : val == other.val || val.equals(other.val);
	}

	@Override
	public XNativeTerm subst(XTerm y, XVar x, boolean propagate) {
	    return super.subst(y, x, propagate);
	}
	
	@Override
	public String instance() {return toString();}

	XNativeVar[] vars;
	/** In case this is a field selection x.f1...fn, return x, x.f1, x.f1.f2, ... x.f1.f2...fn */
	@Override
	public XNativeVar[] vars() {
	    if (vars==null) vars = new XNativeVar[]{this};
	    return vars;
	}

	/** In case this is a field selection x.f1...fn, return x, else this. */
	public XNativeVar rootVar() {return this;}
}
