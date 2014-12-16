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

package x10.types.constraints;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Copy;
import polyglot.util.InternalCompilerError;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.types.matcher.Subst;
import x10.types.TypeParamSubst;

/**
 * A representation of a subtype constraint, S <: T or S == T.
 * TODO: Separate out implementation of haszero T constraint.
 *
 */
public class SubtypeConstraint implements Copy, Serializable {
    private static final long serialVersionUID = 4026637095619421750L;
    public enum Kind {
        SUBTYPE, // <:
        EQUAL, // ==
        HASZERO,
        ISREF
    }; // haszero
    
    protected Kind KIND;
    protected Type subtype;
    protected Type supertype;
    public SubtypeConstraint(Type subtype, Type supertype, Kind kind) {
        this.subtype = subtype;
        this.supertype = supertype;
        this.KIND = kind;
        if (isHaszero() || isIsRef()) assert subtype!=null && supertype==null;
        else assert subtype!=null && supertype!=null;
    }

    public SubtypeConstraint(Type subtype, Type supertype, boolean equals) {
        this(subtype, supertype, equals ? Kind.EQUAL : Kind.SUBTYPE);
    }
    public boolean isEqualityConstraint() { return KIND==Kind.EQUAL; }
    public boolean isSubtypeConstraint()  { return KIND==Kind.SUBTYPE; }
    public boolean isHaszero() {            return KIND==Kind.HASZERO; }
    public boolean isIsRef() {              return KIND==Kind.ISREF; }

    public Kind kind() { return KIND;} 

    /*
     * (non-Javadoc)
     * 
     * @see x10.types.SubtypeConstraint#subtype()
     */
    public Type subtype() {return subtype;}

    /*
     * (non-Javadoc)
     * 
     * @see x10.types.SubtypeConstraint#supertype()
     */
    public Type supertype() {
    	// [DC] sometimes you want to create a new SubtypeConstraint based on an old SubtypeConstraint, so null is OK
        //assert !isHaszero(); // it returns null if it is haszero constraint, so you don't want to call supertype()
        return supertype;
    }

    public SubtypeConstraint copy() {
        try {return (SubtypeConstraint) super.clone();
        } catch (CloneNotSupportedException e) {
            assert false;
            return this;
        }
    }

    public static Type subst(Type t, XTerm y, XVar x) {
        try { return Subst.subst(t, y, x);
        } catch (SemanticException e) {throw new InternalCompilerError(e);}
    }

    /*
     * (non-Javadoc)
     * 
     * @see x10.types.TypeConstraint#subst(x10.constraint.XTerm,
     * x10.constraint.XVar, boolean)
     */
    public SubtypeConstraint subst(XTerm y, XVar x) {
        Type l = subst(subtype, y, x);
        Type r = subst(supertype, y, x);
        if (l == subtype && r == supertype) return this;
        SubtypeConstraint n = copy();
        n.subtype = l;
        n.supertype = r;
        return n;
    }

    @Override public String toString() {
    	if (isHaszero()) {
    		return subtype() + " haszero";
    	} else if (isIsRef()) {
			return subtype() + " isref";
    	} else if (isEqualityConstraint()) {
            return subtype() + " == " + supertype();
    	} else if (isSubtypeConstraint()) {
            return subtype() + " <: " + supertype();
    	} else {
    		throw new RuntimeException("Internal compiler error"); 
    	}
    }
}
