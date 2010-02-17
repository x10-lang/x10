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

package x10.constraint;

import java.util.HashMap;
import java.util.List;


/**
 * 
 *  A constraint solver for the following constraint system:
 * <verbatim>
 * t ::= x                  -- variable
 *       | t.f              -- field access
 *       | g(t1,..., tn)    -- uninterpreted function symbol
 *       
 * c ::= t == t             -- equality
 *       | t != t           -- dis-equality
 *       | c,c              -- conjunction
 *       | p(t1,..., tn)    -- atomic formula
 * </verbatim>  
 * 
 * The constraint system implements the usual congruence rules for equality. 
 * 
 *  <p> TBD:  Add
 *  <verbatim>
 *  t ::= t == t
 *  </verbatim>
 *  
 *  <p> This is useful in specifying that a Region is zeroBased iff its rank=0.
 * 
 * 
 * 
 * @author vj
 * 
 */
public interface XConstraint extends java.io.Serializable{
	
	/**
	 * Is the consistent consistent? That is, does it have a solution?
	 * 
	 * @return true iff the constraint is consistent.
	 */
	boolean consistent();

	/**
	 * Is the constraint valid? That is, is every valuation a solution?
	 * 
	 * @return true iff the constraint is valid.
	 */
	boolean valid();

	/**
	 * Does this entail constraint c in environment sigma.
	 * 
	 * @param t
	 * @return
	 * @throws XFailure
	 */
	boolean entails(XConstraint c);
	boolean entails(XConstraint c, XConstraint sigma);

	/**
	 * Does this entail a==b? 
	 * @param a
	 * @param b
	 * @return
	 * @throws XFailure -- should never be thrown. (TODO: Check this.)
	 */
	boolean entails(XTerm a, XTerm b);
	
	boolean entails(XTerm a);
	
	/**
	 * Does this entail a != b?
	 * @param a
	 * @param b
	 * @return
	 * @throws XFailure -- should never be thrown. (TOOD: Check this.)
	 */
	boolean disEntails(XTerm a, XTerm b);


	/**
	 * Does this entail c, and c entail this? 
	 * 
	 * @param t
	 * @return
	 * @throws XFailure -- should never be thrown. (TODO: Check this.)
	 */
	boolean equiv(XConstraint c) throws XFailure;
	
	/**
	 * Does this entail c, and c entail this, given the constraint sigma?
	 * 
	 * @param c
	 * @return 
	 */
	boolean equiv(XConstraint c, XConstraint sigma);

	
	
	/** Return the term v is bound to in this constraint, and null
	 * if there is no such term. This term will be distinct from v.
	 * */
	XVar bindingForVar(XVar v);




	void setInconsistent();

	//public void addPromise(XTerm p, XPromise node);

	/**
	 * Add t1 -> t2 to the constraint.
	 * 
	 * @param var
	 * @param t
	 * @return constraint with t1=t2 added.
	 * @throws XFailure if the resulting constraint is inconsistent.
	 */
	void addBinding(XTerm var, XTerm val) throws XFailure;
	

	/**
	 * Add t1 != t2 to the constraint.
	 * 
	 * @param var
	 * @param t
	 * @return new constraint with t1 !=t2 added.
	 * @throws XFailure if the resulting constraint is inconsistent
	 */
	void addDisBinding(XTerm var, XTerm val) throws XFailure;
	
	/** Deep copy the constraint. */
	XConstraint copy();


	
	/**
	 * Add the binding term=true to the constraint.
	 * 
	 * @param term -- must be of type Boolean.
	 * @return new constraint with term=true added.
	 * @throws SemanticException
	 */
	void addTerm(XTerm term) throws XFailure;
	void addTerms(List<XTerm> term) throws XFailure;

	/**
	 * Add an atomic formula to the constraint.
	 * @param term
	 * @return
	 * @throws XFailure
	 */
	void addAtom(XTerm term) throws XFailure;

	/**
	 * Look this term up in the constraint graph. Return null if the term
	 * does not exist.
	 * 
	 * @param term
	 * @return
	 * @throws XFailure
	 */
	XPromise lookup(XTerm term) throws XFailure;

	/**
	 * Return in HashMap a set of bindings t1-> t2 equivalent to the current
	 * constraint. Equivalent to constraints(new HashMap()).
	 * 
	 * @return
	 * @throws XFailure
	 */
	List<XTerm> constraints();
	/**
	 * Return in HashMap a set of bindings t1-> t2 equivalent to the current
	 * constraint except that equalities involving EQV variables are ignored.
	 * 
	 * @return
	 * @throws XFailure
	 */
	List<XTerm> extConstraints();


	/**
	 * If y equals x, or x does not occur in this, return this, else copy
	 * the constraint and return it after performing applySubstitution(y,x).
	 * 
	 * 
	 */
	XConstraint substitute(XTerm y, XRoot x) throws XFailure;

	/**
	 * xs and ys must be of the same length. Perform substitute(ys[i],
	 * xs[i]) for each i < xs.length.
	 */
	XConstraint substitute(XTerm[] ys, XRoot[] xs) throws XFailure;

	/**
	 * Perform substitute(y, x) for every binding x -> y in bindings.
	 * 
	 */
	XConstraint substitute(HashMap<XRoot, XTerm> bindings) throws XFailure;

	/**
	 * Preconditions: x occurs in this. It must be the case that the real
	 * clause of the type of y, S, entails the real clause of the type of x,
	 * T. Assume that this and S are fully explicit, that is the
	 * consequences of the types U of variables v occurring in them have
	 * been added to them.
	 * 
	 * Replace all occurrences of x in this by y. For every binding y.p = t
	 * in the result, for every binding self.p.q = t1 in S add
	 * y.p.q=t1[y/self] to the result. Return this now fully explicit
	 * constraint.
	 * 
	 * @param y
	 * @param x
	 */
	void applySubstitution(XTerm y, XRoot x) throws XFailure;

	void applySubstitution(HashMap<XRoot, XTerm> bindings) throws XFailure;

	/**
	 * Does this constraint contain occurrences of the variable v?
	 * 
	 * @param v
	 * @return true iff v is a root variable of this.
	 */
	boolean hasVar(XRoot v);


	
	/**
	 * Return the list of existentially quantified variables in this constraint.
	 * @return
	 */
	List<XVar> eqvs();
	
	/**
	 * Return the list of atoms (atomic formulas) in this constraint.
	 * @return
	 */
	List<XFormula> atoms();
	

	
	/**
	 * Return the least upper bound of this and other. That is, the resulting constraint has precisely
	 * the constraints entailed by both this and other.
	 * @param other
	 * @return
	 */
	XConstraint leastUpperBound(XConstraint other);
	
}
