/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.HashMap;
import java.util.List;

/**
 * An implementation of a simple constraint system. The only constraints expressible are
 * x=a, where x is a variable and a is a constant. In particular, variable=variable 
 * constraints are not expressible.
 * 
 * A null constraint is treated as true (valid).
 * @author vj
 *
 */
public interface Constraint extends java.io.Serializable
{
	/**
	 * Is the consistent consistent? That is, does it have a solution?
	 * @return true iff the constraint is consistent.
	 */
	boolean consistent();
	HashMap<C_Term,Promise> roots();
	
	/**
	 * Is the constraint valid? That is, is every valuation a solution?
	 * @return true iff the constraint is valid.
	 */
	boolean valid();
	/**
	 * Are the two constraints equivalent? That is, do they have the same set of solutions.
	 * @param t
	 * @return
	 * @throws Failure 
	 */
	boolean entails(Constraint t) throws Failure;
	/**
	 * Does this constraint entail the given atomic formula t?
	 * @param t
	 * @return
	 * @throws Failure 
	 */
	boolean entails(C_Term t) throws Failure;
	boolean entails(List<C_Term> list) throws Failure;
	/**
	 * this is entailedby t iff t entails this.
	 * @param t
	 * @return
	 * @throws Failure 
	 */
	boolean entailedBy(Constraint t) throws Failure;
	/**
	 * Do the two constraints entail each other?
	 * @param t
	 * @return
	 * @throws Failure 
	 */
	boolean equiv(Constraint t) throws Failure;
	/**
	 * Does the constraint entail var=val?
	 * @param var
	 * @param t
	 * @return
	 * @throws Failure 
	 */
	boolean entails(C_Term var, C_Term val) throws Failure;
	
	/**
	 * Return the term this variable is bound to in the constraint, and null if there is no such term.
	 * @param varName
	 * @return
	 * @throws Failure 
	 */
	C_Term find(C_Name varName) throws Failure;
	
	void addSelfBinding(C_Var var) throws Failure;

	void setInconsistent();
	
	public void addPromise(C_Term p, Promise node);

	/**
	 * Add t1 -> t2 to the constraint.
	 * @param var
	 * @param t
	 * @return new constraint with t1=t2 added.
	 * @throws Failure 
	 */
	Constraint addBinding(C_Term var, C_Term val) throws Failure;
	/**
	 * For each pair (t1,t2) in result, add t1 -> t2 to the constraint,
	 * and return the resulting constraint.
	 * @param var
	 * @param t
	 * @return new constraint with t1=t2 added.
	 */
	Constraint addConstraints(List<C_Term> terms) throws Failure;

	Constraint copy();
	Constraint clone();
	/**
	 * Add constraint c into this, and return this.
	 * @param c
	 * @return
	 */
	Constraint addIn(Constraint c) throws Failure;
	/**
	 * Add the binding term=true to the constraint.
	 * @param term -- must be of type Boolean.
	 * @return new constraint with term=true added.
	 * @throws SemanticException
	 */
	Constraint addTerm(C_Term term) throws Failure;
	Constraint addAtom(C_Term term) throws Failure;
	C_Var selfVar();
	void setSelfVar(C_Var val);
	
	/** Return the promise obtained by interning this term in the constraint.
	 * This may result in new promises being added to the graph maintained
	 * by the constraint. 
	 * term: Literal -- return the literal.
	 * term: LocalVariable, Special, Here Check if term is already in the roots maintained by
	 * the constraint. If so, return the root, if not add a promise to the roots
	 * and return it.
	 * term: C_Field. Start with the rootVar x and follow the path f1...fk, if term=x.f1...fk.
	 * If the graph contains no nodes after fi, for some i < k, add promises into the
	 * graph from fi+1...fk. Return the last promise.
	 * @param term
	 * @return
	 * @throws Failure 
	 */
	Promise intern(C_Term term) throws Failure ;

	/** Look this term up in the constraint graph.  If the term is of the form x.f1...fk
	 * and the longest prefix that exists in the graph is x.f1..fi, return the promise
	 * corresponding to x.f1...fi. If the promise is a Promise_c, the caller must invoke
	 * lookupReturnValue() to determine if the match was partial (value returned is not
	 * equal to the length of term.vars()). If not even a partial match is found, or the
	 * partial match terminates in a literal (which, by definition, cannot have 
	 * fields), then return null. 
	 *  @seeAlso lookup(C_term term)
	 * @param term
	 * @return
	 * @throws Failure 
	 */
	Promise lookupPartialOk(C_Term term) throws Failure;
	
	/** Look this term up in the constraint graph.  Return null if the term does not exist.
	 *  
	 * @param term
	 * @return
	 * @throws Failure 
	 */
	Promise lookup(C_Term term) throws Failure;
	
	/**
	 * Return in HashMap a set of bindings t1-> t2 equivalent to the current constraint.
	 * Equivalent to constraints(new HashMap()).
	 * @return
	 * @throws Failure 
	 */
	List<C_Term> constraints();
	/**
	 * Return in HashMap a set of bindings t1 -> t2 entailed by the current cosntraint,
	 * where y is a variable that occurs in this, and all terms t1 are of the form
	 * y.p, for some possibly empty path (sequence of fields) p. 
	 * @param y
	 * @return
	 * @throws Failure 
	 */
	List<C_Term> constraints(C_Term y) throws Failure;
	/**
	 * Return result, after adding to it a set of bindings t1-> t2 equivalent to the 
	 * current constraint. 
	 * Equivalent to constraints(result, null, null).
	 * @return
	 * @throws Failure 
	 */
	List<C_Term> constraints(List<C_Term> result);
	/**
	 * Return result, after adding to it a set of bindings t1-> t2 equivalent to the 
	 * current constraint, with all occurrences of self in t1 and t2 replaced by
	 * newSelf (provided that newSelf !=null), and all occurrences of this in t1 and t2
	 * replaced by newThis (provided that newThis !=null).
	 * @return
	 */
	List<C_Term> constraints(List<C_Term> result, C_Var newSelf);
	List<C_Term> constraints(C_Term y, C_Var newSelf);
	
	/**
	 * Generate a new existentially quantified variable scoped to this constraint, 
	 * with the given type.
	 * @return
	 */
	C_EQV genEQV(boolean isSelfVar);
	C_EQV genEQV(boolean isSelfVar, boolean hidden);
	
	/** 
	 * If y equals x, or x does not occur in this,
	 * return this, else copy the constraint
	 * and return it after performing applySubstitution(y,x).
	 * 
	 * 
	 */
	Constraint substitute(C_Term y, C_Root x) throws Failure;
	/** 
	 * xs and ys must be of the same length.
	 * Perform substitute(ys[i], xs[i]) for each i < xs.length.
	 */
	Constraint substitute(C_Term[] ys, C_Root[] xs) throws Failure;
	/** 
	 * Perform substitute(y, x) for every binding x -> y in bindings.
	 * 
	 */
	Constraint substitute(HashMap<C_Root, C_Term> bindings) throws Failure;
	/**
	 * Preconditions: x occurs in this.
	 * It must be the case that the real clause of the
	 * type of y, S, entails the real clause of the type of x, T.
	 * Assume that this and S are fully explicit, that is the
	 * consequences of the types U of variables v occurring in them
	 * have been added to them. 
	 * 
	 * Replace all occurrences of x in this by y. 
	 * For every binding y.p = t in the result, for every binding
	 * self.p.q = t1 in S add y.p.q=t1[y/self] to the result.
	 * Return this now fully explicit constraint.
	 * @param y
	 * @param x
	 */
	void applySubstitution(C_Term y, C_Root x) throws Failure;
	void applySubstitution(HashMap<C_Root, C_Term> bindings) throws Failure;
	
	/**
	 * Does this constraint contain occurrences of the variable v?
	 * @param v
	 * @return true iff v is a root variable of this.
	 */
	boolean hasVar(C_Root v);
	
	int eqvCount();
	Promise internBaseVar(C_Var baseVar, boolean replaceP, Promise last) throws Failure;
}
