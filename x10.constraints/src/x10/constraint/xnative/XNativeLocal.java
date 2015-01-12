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

import x10.constraint.XLocal;
import x10.constraint.XVar;


/**
 * The representation of a local variable reference in the constraint system.
 * The name of the local variable is supplied by an XName. Two XLocal's are equal
 * if their contained XName's are equal.
 * 
 * @author vj
 * 
 */
public class XNativeLocal<T> extends XRoot implements XLocal<T>  {
    private static final long serialVersionUID = 4760673161489125816L;
    public final T name;
	public XNativeLocal(T name) {
	    assert name != null;
		this.name = name;
	}
	
	@Override public int hashCode() {return name.hashCode();}
	public boolean hasVar(XVar v) {return equals(v);}
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof XNativeLocal) {
			XNativeLocal<?> other = (XNativeLocal<?>) o;
			return name.equals(other.name());
		}
		return false;
	}

	@Override
	public T name() {return name;}
    @Override
    public boolean okAsNestedTerm() {return true;}
	@Override
	public String toString() {
		String s = name.toString();
		// This could should not belong here.
		if (s.startsWith("self")) return "self";
		if (s.startsWith("this")) return "this";
		return s;
	}
	
}
