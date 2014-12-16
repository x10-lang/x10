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

import java.util.Collections;
import java.util.List;

import polyglot.ast.Typed;
import polyglot.types.Type;

import x10.constraint.XEQV;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeTerm;
import x10.constraint.xnative.XNativeVar;
import x10.constraint.xnative.XRoot;
import x10.types.constraints.CThis;


/**
 * An optimized representation of this variables.
 * Keeps an int index and a type. Equality involves only
 * int equality.
 * 
 * <p>Code is essentially a copy of CSelf. Do not want to
 * combine, for that will mean extra state needs to be kept
 * with each object.
 * @author vj
 *
 */
public class CNativeThis extends XRoot implements CThis, Typed {
    private static final long serialVersionUID = -2033423584924662939L;
    public static final String THIS_VAR_PREFIX="this";
    
    public final int num;
    //CHECK: The outer qualifier information is now carried in an
    // enclosing QualifiedVar.
    public final Type type;
    public CNativeThis(int n, Type type) {
        this.num = n;
        this.type = type;
    }
    @Override public int hashCode() {return num;}
    /**
     * Return the type of this.
     * Note: A Qualified this, A.this, is represented as a CThis (this) wrapped inside
     * a QualifiedVar (with qualifier A). A.this's type is the type of the QualifiedVar,
     * namely, A.
     */
    public Type type() {return type;}
    public XNativeTerm subst(XTerm y, XVar x) {
       XNativeTerm r = super.subst(y, x);
       return r;
    }
    public boolean okAsNestedTerm() {return true;}
    public boolean hasVar(XVar v) {return equals(v);}
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof CNativeThis) return num == ((CNativeThis) o).num;
        return false;
    }
    @Override public String toString() {
    	return THIS_VAR_PREFIX + (type != null ? "(:" + type + ")" : "");
    }
}
