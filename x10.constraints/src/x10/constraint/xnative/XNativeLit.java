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

package x10.constraint.xnative;

import x10.constraint.XConstraintSystem;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XVar;

/**
 * A representation of a literal. A literal is an XVar carrying a
 * payload that is equal to any other XLit carrying the same payload.
 * 
 * This class and its subclasses should not have mutable state.
 * @author vijay
 *
 */
public class XNativeLit<T extends XType, V> extends XNativeTerm<T> implements XLit<T,V>  {
    private static final long serialVersionUID = 566522638402563631L;
    final protected V val;
    
/* These can only be implemented by the concrete type CNativeLit (or whatever)
    public static XNativeLit TRUE = new XNativeLit(true);
    public static XNativeLit FALSE = new XNativeLit(false);
    public static XNativeLit NULL = new XNativeLit(null);
*/
    
	public XNativeLit(V l, T type) {
		super(type);
		val = l;
	}
    
	public XNativeLit(XNativeLit<T,V> other) {
		super(other);
		val = other.val();
	}
	
	@Override
	public V val() {return val;}
	/**
	 * Pro-actively intern literals since they may end up having fields.
	 */

	// public boolean hasDisBindings() { return false; }

	//@Override
	//public XTermKind kind()  {return XTermKind.LITERAL;}
	/*
	@Override
	public List<XNativeEQV<T>> eqvs() {return Collections.emptyList();}
*/
	
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
	public boolean hasVar(XVar<T> v) { return v.equals(this);}

	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof XNativeLit<?,?>)) return false;
	    // no reified generics so no way to check V -- just check val directly instead
	    XNativeLit<?,?> other = (XNativeLit<?,?>) o;
	    if (val == null) return other.val == null; // avoid NPE on the next line
	    return val.equals(other.val);
	}


	@Override
	public XNativeTerm<T> subst(XConstraintSystem<T> sys, XTerm<T> t1, XTerm<T> t2) {
		return equals(t2) ? (XNativeTerm<T>)t1 : this;
	}	

	public String instance() {return toString();}

	/*
	XNativeTerm<T>[] vars;
	*/
	/** In case this is a field selection x.f1...fn, return x, x.f1, x.f1.f2, ... x.f1.f2...fn */
	/*
	@Override
	public XNativeTerm<T>[] vars() {
	    if (vars==null) vars = new XNativeTerm<T>[]{this};
	    return vars;
	}
	*/

	/** In case this is a field selection x.f1...fn, return x, else this. */
	//@Override
	//public XNativeTerm<T> rootVar() {return this;}

	@Override
	public boolean isProjection() {
		return false;
	}

	@Override
	public XNativeLit<T,V> copy() {
		return new XNativeLit<T,V>(this);
	}

}
