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

import java.util.List;
import java.util.Set;

import x10.constraint.visitors.XGraphVisitor;
/**
 * General interface for constraints. 
 * @author lshadare
 *
 */
public interface XConstraint {
	/**
	 * Check if the constraint is currently consistent. 
	 * @return true if consistent.
	 */
    public boolean consistent(); 
    /**
     * Check if the constraint is valid.
     * @return true if valid. 
     */
    public boolean valid(); 
    /**
     * Strengthen the constraint by adding in the equality
     * left == right. 
     * @param left
     * @param right
     */
    public void addBinding(XTerm left, XTerm right);
    /**
     * Strengthen the constraint by adding in the disequality
     * left != right
     * @param left
     * @param right
     */
    public void addDisBinding(XTerm left, XTerm right);
    /**
     * Strengthen the constraint by asserting the atom t.  
     * @param t
     * @throws XFailure
     */
    public void addAtom(XTerm t) throws XFailure;
    
    /**
     * Strengthen the constraint by adding an general term.
     * Note that the term t should be of Boolean type. 
     * @param t
     * @throws XFailure
     */
    public void addTerm(XTerm t) throws XFailure; 

    /**
     * Check if the current constraint entails other.
     * @param other
     * @return true if the entailment holds.
     */
    public boolean entails(XConstraint other);
    /**
     * Check if the current constraint entails the term.
     * @param term
     * @return true if the entailment holds. 
     */
    public boolean entails(XTerm term);
    /**
     * Check if the current constraint entails the disequality
     * left != right 
     * @param left
     * @param right
     * @return true if the disequality is entailed 
     */
    public boolean disEntails(XTerm left, XTerm right);
    /**
     * Check if the current constraint entails the equality
     * left != right 
     * @param left
     * @param right
     * @return true if the equality is entailed 
     */
    public boolean entails(XTerm left, XTerm right);
    
    /**
	 * Return the least upper bound of this and other. That is, the resulting 
	 * constraint has precisely the constraints entailed by both this and other.
	 * (Note: An inconsistent constraint entails every constraint, and 
	 * a valid constraint entails only those constraints such as x=x that 
	 * every constraint entails.)
	 * @param other
	 * @return
	 */
    public XConstraint leastUpperBound(XConstraint other);
    
     /**
     * Return those subset of constraints in the base set of other that are 
     * <em>not</em> implied by this. That is, return the residue
     * r such that (r and this) implies other.
     * @param other -- must be checked for consistency before call is made
     * @return
     */
    public XConstraint residue(XConstraint other);
    /**
     * Returns a list of XTerms representing the conjuncts in the constraint. 
     * This must not be flatten. 
     * @return
     */
    public List<? extends XTerm> constraints();	
    public List<? extends XFormula<?>> atoms();
    public void visit(XGraphVisitor xg);
    public String toString();
	public XConstraint copy();
	/**
	 * Return a term v is equal to in the constraint, and null if there
	 * is no such term. 
	 * @param v
	 * @return t such that this |- t = v
	 */
	public XVar bindingForVar(XVar v);
	public Set<? extends XTerm> getTerms();
	public void setInconsistent();
	public Set<? extends XVar> vars(); 
	
	/**
     * Return a list of bindings t1-> t2 equivalent to 
     * the current constraint except that equalities involving EQV variables 
     * are ignored.
     * 
     * @return
     */
	public List<? extends XTerm> extConstraints();
	/**
	 * Return a list of bindings t1-> t2 equivalent to the current
	 * constraint except that equalities involving only EQV variables are 
	 * ignored if dumpEQV is false, and equalities involving only fake fields
	 * are ignored if hideFake is true.
	 * 
	 * @return
	 */
	public List<? extends XTerm> extConstraintsHideFake();
}
