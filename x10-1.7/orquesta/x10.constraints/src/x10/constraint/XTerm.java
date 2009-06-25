/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.io.Serializable;
import java.util.Set;

public interface XTerm extends Serializable, Cloneable {
        public XTerm clone();
        
	boolean hasVar(XVar v);
	
	/** Get the constraint on the term's value. */
	XConstraint selfConstraint();
	
	/** Set the constraint on the term's value. */
	void setSelfConstraint(XRef_c<XConstraint> c);

	/** Add in the self-constraint to c, and set the self-constraint to null 
	 * @param visited TODO
	 * @return TODO*/
	boolean saturate(XConstraint c, Set<XTerm> visited) throws XFailure;

	/**
	 * Is this an existentially quantified variable in the constraint?
	 * 
	 * @return true if it is, false if it isn't.
	 */
	boolean isEQV();

	/**
	 * If true, bind this variable when processing this=term, for any term. In
	 * case term also prefers being bound, choose any one.
	 * 
	 * @return
	 */
	boolean prefersBeingBound();

	/**
	 * Is this (= x.f1...fn) a prefix of term, i.e. is term of the form
	 * x.f1...fn.fn+1...fk?
	 * 
	 * @return
	 */
	boolean prefixes(XTerm term);

	XPromise internIntoConstraint(XConstraint constraint, XPromise last)
			throws XFailure;

	/**
	 * Returns true if this term is an atomic formula, i.e. a constraint. Note
	 * that, == constraints are represented specially, i.e. binary terms of the
	 * form t == s are not interned into a constraint as the binary term t==s.
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
