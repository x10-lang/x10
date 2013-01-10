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
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XVar;


/**
 * The representation of a local variable reference in the constraint system.
 * The name of the local variable is supplied by an XName. Two XLocal's are equal
 * if their contained XName's are equal.
 * 
 * @author vj
 * 
 */
public abstract class XNativeVar<T extends XType> extends XNativeTerm<T> implements XVar<T>  {

	private static final long serialVersionUID = 4760673161489125816L;
    
	public final String name;
	
	public XNativeVar(T type, String name) {
		super(type);
	    assert name != null;
		this.name = name;
	}
	
	public XNativeVar(XNativeVar<T> other) {
		this(other.type(), other.name);
	}

	@Override
	public int hashCode() {return name.hashCode();}

	@Override
	public boolean hasVar(XVar<T> v) {return equals(v);}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof XNativeVar) {
			XNativeVar<?> other = (XNativeVar<?>) o;
			return name.equals(other.name());
		}
		return false;
	}

	@Override
	public String name() {return name;}
    
	@Override
    public boolean okAsNestedTerm() {return true;}

    @Override
	public String toString() {
		String s = name;
		/*
		// This could should not belong here.
		if (s.startsWith("self")) return "self";
		if (s.startsWith("this")) return "this";
		*/
		return s;
	}

	@Override
	public boolean isProjection() {
		return false;
	}

//	@Override
//	public XTerm<T> copy() {
//		return new XNativeLocal<T>(this);
//	}

	@Override
	public XNativeTerm<T> subst(XConstraintSystem<T> sys, XTerm<T> t1, XTerm<T> t2) {
		if (this.equals(t2))
			return (XNativeTerm<T>)t1;
		return this; 	
	}

	
}
