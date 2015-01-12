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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import x10.constraint.XTerm;
import x10.constraint.XVar;


/**
 * Constraints constrain XTerms. Thus XTerms are the basic building blocks 
 * of constraints.This class is the root class of constraint terms.
 * Class should not have any state.
 * 
 * @author njnystrom
 * @author vj
 *
 */
public abstract class XNativeTerm implements XTerm, Serializable, Cloneable {
    private static final long serialVersionUID = -3094676101576926720L;
    public XNativeTerm() {super();}

	// The default is OBJECT. May be overridden by subclasses.
	public XTermKind kind() { return XTermKind.OBJECT;}
	
	/**
	 * Return the result of substituting the term y for x in this.
	 * Should be overridden in subtypes.
	 * @param y
	 * @param x
	 * @return
	 */
	@Override
	public XNativeTerm subst(XTerm y, XVar x) {
		return subst(y, x, true);
	}
	
	/**
	 * Returns true only if this term is allowed to occur inside a constraint.
	 * Terms a&&b, a||b, a==b etc must return false.
	 * @return
	 */
	@Override
	public abstract boolean okAsNestedTerm();

	// int nextId = 0;
	
	/**
	 * Return the result of substituting y for x in this.
	 * 
	 * @param y --
	 *            the value to be substituted
	 * @param x --
	 *            the variable which is being substituted for
	 * @return the term with the substitution applied
	 */
	public XNativeTerm subst(XTerm y, final XVar x, boolean propagate) {
	    XNativeTerm t = this;
	    return t;
	}

	@Override
	public XNativeTerm clone() {
		try {
			XNativeTerm n = (XNativeTerm) super.clone();
			return n;
		}
		catch (CloneNotSupportedException e) {
			return this;
		}
	}

	/**
	 * Does this contain an existentially quantified variable?
	 * Default no; should be overridden by subclasses representing eqvs.
	 * @return true if it is, false if it isn't.
	 */
	@Override
	public boolean hasEQV() {return false;}
	
	/**
	 * Is this itself an EQV?
	 * Default no; should be overridden by subclasses representing eqvs.
	 * @return
	 */
	public boolean isEQV() {return false;}
	public List<XNativeEQV> eqvs() {return Collections.emptyList();}

	/**
	 * Is <code>this</code> a prefix of <code>term</code>, i.e. is 
	 * <code>term</code> of the form <code>this.f1...fn</code>?
	 * Default no; should be overridden by subclasses.
	 * @return
	 */
	/*public boolean prefixes(XTerm term) {
		return false;
	}*/

	public static final int TERM_MUST_NOT_BE_BOUND=-1;
	public static final int TERM_PREFERS_BEING_BOUND=1;
	public static final int TERM_SHRUGS_ABOUT_BEING_BOUND=0;
	/**
	 * 0 == dont care, bind me if you want
	 * -1 == must not bind me!
	 * If true, bind this variable when processing this=t, for
	 * any term t. In case t also prefers being bound, choose any
	 * one.
	 * 
	 * @return true if this  prefers being bound in a constraint this==t.
	 */
	public int prefersBeingBound() {return TERM_SHRUGS_ABOUT_BEING_BOUND;}

	/**
	 * Returns true if this term is an atomic formula.
	 *  == constraints are represented specially, and not considered atomic formulas.
	 * 
	 * @return true -- if this term represents an atomic formula
	 */
	@Override
	public boolean isAtomicFormula() {return false;}


	/** 
	 * Returns true if the variable v occurs in this term.
	 * @param v -- the variable being checked.
	 * @return true if v occurs in this
	 */
	@Override
	public boolean hasVar(XVar v) {return true;}

	/**
       Intern this term into constraint and return the promise
       representing the term. 
       
       <p> Throw an XFailure if the resulting constraint is inconsistent.
	 */
	abstract XPromise internIntoConstraint(XNativeConstraint constraint, XPromise last);

	@Override
	public abstract int hashCode();
	@Override
	public abstract boolean equals(Object o);

    /**
     * Given a visitor, we traverse the entire term (which is like a tree).
     * @param visitor
     * @return If the visitor didn't return any new child, then we return "this"
     *  (otherwise we create a clone with the new children)
     */
	@Override
    public XNativeTerm accept(TermVisitor visitor) {
        // The default implementation for "leave" terms (that do not have any children)
        XNativeTerm res = (XNativeTerm)visitor.visit(this);
        if (res!=null) return res;
        return this;
    }
    
    /**
     * Return the normal form for this term in this given constraint.
     * The normal form of a term t in a constraint c, t.nf(c), is a term 
     * s with the property that 
     * for all u: s=u.nf(c) iff c |- s=u
     * From this it follows that s=s.nf(c).
     * The normal form is computed as nfp(c).term().
     * @param c
     * @return
     */
    public final XNativeTerm nf(XNativeConstraint c) {
    	assert c != null;
    	return nfp(c).term();
    }
    
    /**
     * Return the promise corresponding to the normal form of the term, 
     * interning the term if it is not interned already. 
     * If p is the return value, then guaranteed p!= null and p=p.lookup().
     * @param c
     * @return
     */
    public abstract XPromise nfp(XNativeConstraint c);
    
    @Override
    public boolean isLit()   {return false;} 
    @Override
    public boolean isSelf()  {return false;}
    @Override
    public boolean isThis()  {return false;}
    @Override
    public boolean isField() {return false;}
    @Override
    public boolean isBoolean() {
    	return this == XNativeLit.TRUE || this == XNativeLit.FALSE;
    }
    @Override
    public String toString() {
    	return "Native term !!"; 
    }
}
