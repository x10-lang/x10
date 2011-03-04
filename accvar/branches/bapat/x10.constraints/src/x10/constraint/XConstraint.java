/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.HashMap;
import java.util.List;

/**
 * An implementation of a simple constraint system. The only constraints
 * expressible are x=a, where x is a variable and a is a constant. In
 * particular, variable=variable constraints are not expressible.
 * 
 * A null constraint is treated as true (valid).
 * 
 * @author vj
 * 
 */
public interface XConstraint extends java.io.Serializable, ThisVar {
        /**
         * Variable to use for self in the constraint.
         */
        XRoot self();
        
        
        void setThisVar(XVar thisVar);
        void addThisBinding(XTerm term) throws XFailure;
    
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
	boolean entails(XConstraint c) throws XFailure;
	boolean entails(XConstraint c, XConstraint sigma) throws XFailure;

	boolean entails(XTerm a, XTerm b) throws XFailure;
	
	boolean disEntails(XTerm a, XTerm b) throws XFailure;


	/**
	 * Do the two constraints entail each other?
	 * 
	 * @param t
	 * @return
	 * @throws XFailure
	 */
	boolean equiv(XConstraint c) throws XFailure;
	boolean equiv(XConstraint c, XConstraint sigma) throws XFailure;

	/**
	 * Does the constraint entail var=val?
	 * 
	 * @param var
	 * @param t
	 * @return
	 * @throws XFailure
	 */
	// boolean entails(XTerm var, XTerm val) throws XFailure;
	/**
	 * Return the term this variable is bound to in the constraint, and null
	 * if there is no such term.
	 * 
	 * @param varName
	 * @return
	 * @throws XFailure
	 */
	XTerm find(XName varName) throws XFailure;

	void addSelfBinding(XTerm var) throws XFailure;

	void setInconsistent();

	public void addPromise(XTerm p, XPromise node);

	/**
	 * Add t1 -> t2 to the constraint.
	 * 
	 * @param var
	 * @param t
	 * @return new constraint with t1=t2 added.
	 * @throws XFailure
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

	/**
	 * For each pair (t1,t2) in result, add t1 -> t2 to the constraint, and
	 * return the resulting constraint.
	 * 
	 * @param var
	 * @param t
	 * @return new constraint with t1=t2 added.
	 */
	// XConstraint addConstraints(List<XTerm> terms) throws XFailure;
	
	/** Deep copy the constraint. */
	XConstraint copy();

//	/** Shallow copy the constraint. */
//	XConstraint clone();

	/**
	 * Add constraint c into this, and return this.
	 * No change is made to this if c==null
	 * 
	 * @param c
	 * @return
	 */
	XConstraint addIn(XConstraint c) throws XFailure;

	/**
	 * Add the binding term=true to the constraint.
	 * 
	 * @param term --
	 *                must be of type Boolean.
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

	/** Return x where this constraint has v==x. */
	XVar bindingForVar(XVar v);

	/**
	 * Return the promise obtained by interning this term in the constraint.
	 * This may result in new promises being added to the graph maintained
	 * by the constraint. 
	 * <p>term: Literal -- return the literal. 
	 * <p> term:LocalVariable, Special, Here Check if term is already in the roots
	 * maintained by the constraint. If so, return the root, if not add a
	 * promise to the roots and return it. 
	 * <p> term: XField. Start with the rootVar x and follow the path f1...fk, 
	 * if term=x.f1...fk. If the graph contains no nodes after fi, 
	 * for some i < k, add promises into the graph from fi+1...fk. 
	 * Return the last promise.
	 * 
	 * @param term
	 * @return
	 * @throws XFailure
	 */
	XPromise intern(XTerm term) throws XFailure;

	/**
	 * Look this term up in the constraint graph. If the term is of the form
	 * x.f1...fk and the longest prefix that exists in the graph is
	 * x.f1..fi, return the promise corresponding to x.f1...fi. If the
	 * promise is a Promise_c, the caller must invoke lookupReturnValue() to
	 * determine if the match was partial (value returned is not equal to
	 * the length of term.vars()). If not even a partial match is found, or
	 * the partial match terminates in a literal (which, by definition,
	 * cannot have fields), then return null.
	 * 
	 * @seeAlso lookup(C_term term)
	 * @param term
	 * @return
	 * @throws XFailure
	 */
	XPromise lookupPartialOk(XTerm term) throws XFailure;

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
	 * Generate a new existentially quantified variable scoped to this
	 * constraint.
	 * 
	 * @return
	 */
	XEQV genEQV();
	XEQV genEQV(boolean hidden);
	XEQV genEQV(XName name, boolean hidden);

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

	XPromise internBaseVar(XVar baseVar, boolean replaceP, XPromise last) throws XFailure;

//        @Deprecated
//	XConstraint saturate() throws XFailure;
	
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
}
