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

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Constraints constrain XTerms. Thus XTerms are the basic building blocks of constraints
 * 
 * @author njnystrom
 * @author vj
 *
 */
public interface XTerm extends Serializable, Cloneable {
	public XTerm clone();
	List<XEQV> eqvs();

	Solver solver();
	XTermKind kind();

	/** 
	 * Returns true if the variable v occurs in this term.
	 * @param v -- the variable being checked.
	 * @return true if v occurs in this
	 */
	boolean hasVar(XVar v);

	/**
	 * Does this contain an existentially quantified variable?
	 * 
	 * @return true if it is, false if it isn't.
	 */
	boolean hasEQV();

	/**
	 * Is this itself an EQV?
	 * @return
	 */
	boolean isEQV();

	/**
	 * If true, bind this variable when processing this=t, for
	 * any term t. In case t also prefers being bound, choose any
	 * one.
	 * 
	 * @return true if this  prefers being bound in a constraint this==t.
	 */
	boolean prefersBeingBound();

	/**
	 * Is <code>this</code> a prefix of <code>term</code>, i.e. is <code>term</code> of the form 
	 * <code>this.f1...fn</code>?
	 * 
	 * @return
	 */
	boolean prefixes(XTerm term);

	/**
       Intern this term into constraint and return the promise
       representing the term. 
       
       <p> Throw an XFailure if the resulting constraint is inconsistent.
	 */
	XPromise internIntoConstraint(XConstraint_c constraint, XPromise last)
	throws XFailure;

	/**
	 * Returns true if this term is an atomic formula.
	 *  == constraints are represented specially, and not considered atomic formulas.
	 * 
	 * @return true -- if this term represents an atomic formula
	 */
	boolean isAtomicFormula();

	void markAsAtomicFormula();

	/**
	 * Return the result of substituting y for x in this.
	 * 
	 * @param y --
	 *            the value to be substituted
	 * @param x --
	 *            the variable which is being substituted for
	 * @return the term with the substitution applied
	 */
	XTerm subst(XTerm y, XRoot x);
	XTerm subst(XTerm y, XRoot x, boolean propagate);
}
