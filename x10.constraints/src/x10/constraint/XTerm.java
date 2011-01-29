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
import java.util.Collections;
import java.util.List;

/**
 * Constraints constrain XTerms. Thus XTerms are the basic building blocks 
 * of constraints.This class is the root class of constraint terms.
 * Class should not have any state.
 * 
 * @author njnystrom
 * @author vj
 *
 */
public abstract class XTerm implements  Serializable, Cloneable {

	public XTerm() {
		super();
	}

	// The default is OBJECT. May be overridden by subclasses.
	public XTermKind kind() { return XTermKind.OBJECT;}
	
	/**
	 * Return the result of substituting the term y for x in this.
	 * Should be overridden in subtypes.
	 * @param y
	 * @param x
	 * @return
	 */
	public final XTerm subst(XTerm y, XVar x) {
	    return subst(y, x, true);
	}

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
	public XTerm subst(XTerm y, final XVar x, boolean propagate) {
	    XTerm t = this;
	    return t;
	}

	@Override
	public XTerm clone() {
		try {
			XTerm n = (XTerm) super.clone();
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
	public boolean hasEQV() {
		return false;
	}
	
	/**
	 * Is this itself an EQV?
	 * Default no; should be overridden by subclasses representing eqvs.
	 * @return
	 */
	public boolean isEQV() {
		return false;
	}
	
	public List<XEQV> eqvs() {
	    return Collections.emptyList();
	}

	/**
	 * Is <code>this</code> a prefix of <code>term</code>, i.e. is 
	 * <code>term</code> of the form <code>this.f1...fn</code>?
	 * Default no; should be overridden by subclasses.
	 * @return
	 */
	/*public boolean prefixes(XTerm term) {
		return false;
	}*/

	/**
	 * If true, bind this variable when processing this=t, for
	 * any term t. In case t also prefers being bound, choose any
	 * one.
	 * 
	 * @return true if this  prefers being bound in a constraint this==t.
	 */
	public boolean prefersBeingBound() {
		return false;
	}

	/**
	 * Returns true if this term is an atomic formula.
	 *  == constraints are represented specially, and not considered atomic formulas.
	 * 
	 * @return true -- if this term represents an atomic formula
	 */
	public boolean isAtomicFormula() {
	    return false;
	}


	/** 
	 * Returns true if the variable v occurs in this term.
	 * @param v -- the variable being checked.
	 * @return true if v occurs in this
	 */
	//public abstract boolean hasVar(XVar v);

	/**
       Intern this term into constraint and return the promise
       representing the term. 
       
       <p> Throw an XFailure if the resulting constraint is inconsistent.
	 */
	abstract XPromise internIntoConstraint(XConstraint constraint, XPromise last);

	public abstract int hashCode();
	public abstract boolean equals(Object o);

}
