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

import java.io.Serializable;

import x10.constraint.XCommonTerm;
import x10.constraint.XConstraintSystem;
import x10.constraint.XTerm;
import x10.constraint.XType;


/**
 * Constraints constrain XTerms. Thus XTerms are the basic building blocks 
 * of constraints.This class is the root class of constraint terms.
 * Class should not have any state.
 * 
 * @author njnystrom
 * @author vj
 *
 */
public abstract class XNativeTerm<T extends XType> extends XCommonTerm<T> implements XTerm<T>, Serializable, Cloneable {
    private static final long serialVersionUID = -3094676101576926720L;

    
    public XNativeTerm(T type) {
    	super(type);
    }
    
    public XNativeTerm(XNativeTerm<T> other) {
		this(other.type());
	}

    // Tighten the return type
    @Override public abstract XNativeTerm<T> subst(XConstraintSystem<T> sys, XTerm<T> t1, XTerm<T> t2);
	@Override public XNativeTerm<T> accept(TermVisitor<T> visitor) { return (XNativeTerm<T>) super.accept(visitor); }

	
	
	public abstract XNativeTerm<T> copy();
    
}
