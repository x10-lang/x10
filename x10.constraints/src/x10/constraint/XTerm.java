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

package x10.constraint;

import java.io.Serializable;

/**
 * Constraints constrain XTerms. Thus XTerms are the basic building blocks 
 * of constraints.This interface should be implemented by the terms specific to 
 * all constraint systems. Two different implementations of XTerm should *never* 
 * be mixed. 
 * 
 * @author njnystrom
 * @author vj
 *
 */
public interface XTerm extends  Serializable, Cloneable {

	/**
	 * Return the result of substituting the term y for x in this.
	 * Should be overridden in subtypes.
	 * @param y
	 * @param x
	 * @return
	 */
	public XTerm subst(XTerm y, XVar x);
	
	/**
	 * Returns true only if this term is allowed to occur inside a constraint.
	 * Terms a&&b, a||b, a==b etc must return false.
	 * @return
	 */
	public abstract boolean okAsNestedTerm();

	public XTerm clone();
	
	/**
	 * Returns true if this term is an atomic formula.
	 *  == constraints are represented specially, and not considered atomic formulas.
	 * 
	 * @return true -- if this term represents an atomic formula
	 */
	public boolean isAtomicFormula(); 


	/** 
	 * Returns true if the variable v occurs in this term.
	 * @param v -- the variable being checked.
	 * @return true if v occurs in this
	 */
	public boolean hasVar(XVar v);
	public abstract int hashCode();
	public abstract boolean equals(Object o);

    // Wrote my own visitor, cause the XGraphVisitor is too cumbersome
    public interface TermVisitor {
        /**
         * Visit the term tree.
         * @param term
         * @return  A term if normal traversal is to stop, <code>null</code> if it
         * is to continue.
         */
        XTerm visit(XTerm term);
    }
    /**
     * Given a visitor, we traverse the entire term (which is like a tree).
     * @param visitor
     * @return If the visitor didn't return any new child, then we return "this"
     *  (otherwise we create a clone with the new children)
     */
    public XTerm accept(TermVisitor visitor) ;
    
    public boolean hasEQV(); 

    public boolean isLit(); 
    public boolean isSelf(); 
    public boolean isThis(); 
    public boolean isField(); 
    public boolean isBoolean(); 
    public String toString(); 
}
