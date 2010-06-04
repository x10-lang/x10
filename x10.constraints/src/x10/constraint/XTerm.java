/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * XTerms are the basic building blocks of constraints. 
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
	 * any term t. In case term also prefers being bound, choose any
	 * one.
	 * 
	 * @return true if this  prefers being bound in a constraint this==t.
	 */
	boolean prefersBeingBound();

	/**
	 * Is this (= x.f1...fn, for n >= 0) a prefix of term, i.e. is
	 * term of the form x.f1...fn.fn+1...fk?
	 * 
	 * @return
	 */
	boolean prefixes(XTerm term);

    /**
       Intern this term into constraint and return the promise
       representing the term. Thorw an XFailure if the resulting
       constraint is inconsistent.
     */
	XPromise internIntoConstraint(XConstraint constraint, XPromise last)
			throws XFailure;

	/**
	 * Returns true if this term is an atomic formula.
	 *  == constraints are represented
	 * specially, and not considered atomic formulas.
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
