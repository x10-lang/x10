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

import java.util.Collections;
import java.util.List;

import x10.constraint.XEQV;
import x10.constraint.XVar;




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
public class XNativeEQV extends XRoot implements XEQV {
    private static final long serialVersionUID = -5701688546973148631L;
    public final int num;
    public XNativeEQV(int n) {this.num=n;}
    
    @Override public XTermKind kind() { return XTermKind.LOCAL;}
    @Override public int prefersBeingBound() {return XNativeTerm.TERM_PREFERS_BEING_BOUND;}
    @Override public boolean hasVar(XVar v) {return equals(v);}
    @Override public boolean hasEQV() {return true;}
    @Override public boolean isEQV() {return true;}
    @Override public List<XNativeEQV> eqvs() {return Collections.<XNativeEQV>singletonList(this);}


    @Override public boolean okAsNestedTerm() {return true;}
    @Override public int hashCode() {return num;}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof XNativeEQV) return num == ((XNativeEQV) o).num;
        return false;
    }
    
    @Override public String toString() {return "eqv#" + num;}
}
