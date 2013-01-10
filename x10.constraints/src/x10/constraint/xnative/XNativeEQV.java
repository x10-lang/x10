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

import x10.constraint.XEQV;
import x10.constraint.XType;




/**
 * A representation of logical variables. 
 * 
 * <p> EQVs  are existentially quantified
 * in each constraint in which they occur.

  * <p> UQVs  are free in the constraint and may occur in multiple constraints. 
 * @author vj
 * @see XNativeUQV
 *
 */
public class XNativeEQV<T extends XType> extends XNativeVar<T> implements XEQV<T> {
    private static final long serialVersionUID = -5701688546973148631L;
    public final int num; // num is an optimisation
    public XNativeEQV(T type, int n) {
    	super(type, "eqv#" + n);
        this.num=n;
    }  
    public XNativeEQV(XNativeEQV<T> other) {
    	super(other.type(), other.name);
    	this.num = other.num;
    }  
    
    // [DC] use cached int field (optimisation)
    @Override public int hashCode() {return num;}

    // [DC] use cached int field (optimisation)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof XNativeEQV<?>) return num == ((XNativeEQV<?>) o).num;
        return false;
    }

    public XNativeEQV<T> copy() {
    	return new XNativeEQV<T>(type(),num);
    }
}
