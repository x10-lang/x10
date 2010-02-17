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

package x10.types.constraints;

import java.util.HashMap;

import polyglot.ast.Field;

import x10.constraint.ThisVar;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XName;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XVar;

/**
 * @author vj
 *
 */
public interface CConstraint extends XConstraint, ThisVar  {
	public static final String SELF_VAR_PREFIX="_self";
	/**
	 * Variable to use for self in the constraint.
	 */
	XRoot self();


	void setThisVar(XVar thisVar);
	
	/** Deep copy the constraint. */
	CConstraint copy();
	
	/**
	 * Return the term self.fieldName is bound to in the constraint, and null
	 * if there is no such term.
	 * 
	 * @param fieldName -- Name of field
	 * @return
	 * @throws XFailure
	 */
	XTerm bindingForSelfField(XName fieldName);
	XTerm bindingForSelfField(Field f);
	
	/**
	 * Add constraint c into this, and return this. Note: this is possibly side-effected
	 * as a result of this operation.
	 * No change is made to this if c==null
	 * 
	 * @param c
	 * @return
	 */
	CConstraint addIn(CConstraint c) throws XFailure;
	CConstraint addIn(XTerm newSelf, CConstraint c)  throws XFailure;
	
	boolean entails(CConstraint c, CConstraint sigma) throws XFailure;
	
	void addBinding(XTerm var, XConstrainedTerm val) throws XFailure;
	void addBinding(XConstrainedTerm var, XConstrainedTerm val) throws XFailure;
	void addBinding(XConstrainedTerm var, XTerm val) throws XFailure;
	
	void addSelfBinding(XConstrainedTerm var) throws XFailure;
	/**
	 * If y equals x, or x does not occur in this, return this, else copy
	 * the constraint and return it after performing applySubstitution(y,x).
	 * 
	 * 
	 */
	CConstraint substitute(XTerm y, XRoot x) throws XFailure;

	/**
	 * xs and ys must be of the same length. Perform substitute(ys[i],
	 * xs[i]) for each i < xs.length.
	 */
	CConstraint substitute(XTerm[] ys, XRoot[] xs) throws XFailure;

	/**
	 * Perform substitute(y, x) for every binding x -> y in bindings.
	 * 
	 */
	CConstraint substitute(HashMap<XRoot, XTerm> bindings) throws XFailure;


	/**
	 * Return a new constraint obtained from the current one by substituting
	 * newSelf for this.self(). Note, the resulting constraint may be marked inconsistent.
	 * @param newSelf
	 * @return
	 */
	CConstraint instantiateSelf(XTerm newSelf);
	
	 /* Return the least upper bound of this and other. That is, the resulting constraint has precisely
	 * the constraints entailed by both this and other.
	 * @param other
	 * @return
	 */
	CConstraint leastUpperBound(CConstraint other);
	
	void addThisBinding(XTerm term) throws XFailure;
	void addSelfBinding(XTerm var) throws XFailure;
}
