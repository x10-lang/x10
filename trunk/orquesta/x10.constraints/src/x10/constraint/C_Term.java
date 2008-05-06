/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.io.Serializable;
import java.util.List;

public interface C_Term extends Serializable {
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
	boolean prefixes(C_Term term);

	Promise internIntoConstraint(Constraint constraint, Promise last)
			throws Failure;

	List<C_Var> variables();

	void variables(List<C_Var> result);

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
	C_Term substitute(C_Var y, C_Var x);
}
