/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Expr;
import polyglot.ext.x10.types.X10Type;
import polyglot.types.SemanticException;
import polyglot.types.Type;

/**
 * An implementation of a simple constraint system. The only constraints expressible are
 * x=a, where x is a variable and a is a constant. In particular, variable=variable 
 * constraints are not expressible.
 * 
 * A null constraint is treated as true (valid).
 * @author vj
 *
 */
public interface Constraint extends Serializable {
	/**
	 * Is the consistent consistent? That is, does it have a solution?
	 * @return true iff the constraint is consistent.
	 */
	boolean consistent();
	void setInconsistent();
	HashMap<C_Var, Promise> roots();
	
	/**
	 * Is the constraint valid? That is, is every valuation a solution?
	 * @return true iff the constraint is valid.
	 */
	boolean valid();
	/**
	 * Are the two constraints equivalent? That is, do they have the same set of solutions.
	 * @param t
	 * @return
	 */
	boolean entails(Constraint t);
	/**
	 * Are all the constraints implied by the variable declaration v
	 * already entailed by c? Used in checking that the real clause
	 * supplied by the user is valid.
	 * @param v
	 * @return
	 */
	boolean entailsType(C_Var v);
	/**
	 * this is entailedby t iff t entails this.
	 * @param t
	 * @return
	 */
	boolean entailedBy(Constraint t);
	/**
	 * Do the two constraints entail each other?
	 * @param t
	 * @return
	 */
	boolean equiv(Constraint t);
	/**
	 * Does the constraint entail var=val?
	 * @param var
	 * @param t
	 * @return
	 */
	boolean entails(C_Var var, C_Var val);
	
	/**
	 * Return the term this variable is bound to in the constraint, and null if there is no such term.
	 * @param varName
	 * @return
	 */
	C_Var find(String varName);
	
	/**
	 * Add t1 -> t2 to the constraint.
	 * @param var
	 * @param t
	 * @return new constraint with t1=t2 added.
	 * @throws Failure TODO
	 */
	Constraint addBinding(C_Var var, C_Var val) throws Failure;
	/**
	 * For each pair (t1,t2) in result, ddd t1 -> t2 to the constraint,
	 * and return the resulting constraint.
	 * @param var
	 * @param t
	 * @return new constraint with t1=t2 added.
	 * @throws Failure TODO
	 */
	Constraint addBindings(HashMap<C_Var,C_Var> result) throws Failure;
	
	Constraint copy();
	Constraint clone();
	/**
	 * Add constraint c into this, and return this.
	 * @param c
	 * @return
	 * @throws Failure TODO
	 */
	Constraint addIn(Constraint c) throws Failure;
	/**
	 * Add the binding term=true to the constraint.
	 * @param term -- must be of type Boolean.
	 * @return new constraint with term=true added.
	 * @throws SemanticException
	 */
	Constraint addTerm(C_Var term) throws Failure;
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
	 */
	Promise intern(C_Var term) ;
	void internRecursively(C_Var variable) throws Failure;
	
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
	 */
	Promise lookupPartialOk(C_Term term);
	
	/** Look this term up in the constraint graph.  Return null if the term does not exist.
	 *  
	 * @param term
	 * @return
	 */
	Promise lookup(C_Term term);
	
	/**
	 * Return in Map a set of bindings t1-> t2 equivalent to the current constraint.
	 * Equivalent to constraints(new HashMap()).
	 * @return
	 */
	HashMap<C_Var, C_Var> constraints();
	/**
	 * Return in Map a set of bindings t1 -> t2 entailed by the current constraint,
	 * where y is a variable that occurs in this, and all terms t1 are of the form
	 * y.p, for some possibly empty path (sequence of fields) p. 
	 * @param y
	 * @return
	 */
	HashMap<C_Var, C_Var> constraints(C_Var y);
	/**
	 * Return result, after adding to it a set of bindings t1-> t2 equivalent to the 
	 * current constraint. 
	 * Equivalent to constraints(result, null, null).
	 * @return
	 */
	HashMap<C_Var, C_Var> constraints(HashMap<C_Var,C_Var> result);
	/**
	 * Return result, after adding to it a set of bindings t1-> t2 equivalent to the 
	 * current constraint, with all occurrences of self in t1 and t2 replaced by
	 * newSelf (provided that newSelf !=null), and all occurrences of this in t1 and t2
	 * replaced by newThis (provided that newThis !=null).
	 * @return
	 */
	HashMap<C_Var, C_Var> constraints(HashMap<C_Var,C_Var> result, C_Var newSelf, C_Var newThis);
	HashMap<C_Var, C_Var> constraints(C_Var y, C_Var newSelf);
	
	/**
	 * Generate a new existentially quantified variable scoped to this constraint, 
	 * with the given type.
	 * @return
	 */
	C_EQV genEQV(Type type, boolean isSelfVar);
	C_EQV genEQV(Type type, boolean isSelfVar, boolean hidden);
	
	/** 
	 * If y equals x, or x does not occur in this,
	 * return this, else copy the constraint
	 * and return it after performing applySubstitution(y,x).
	 * @throws Failure TODO
	 * 
	 * 
	 */
	Constraint substitute(C_Var y, C_Root x) throws Failure;
	Constraint substitute(C_Var y, C_Root x, boolean propagate) throws Failure;
	/** 
	 * xs and ys must be of the same length.
	 * Perform substitute(ys[i], xs[i]) for each i < xs.length.
	 * @throws Failure TODO
	 */
	Constraint substitute(C_Var[] ys, C_Root[] xs) throws Failure;
	Constraint substitute(C_Var[] ys, C_Root[] xs, boolean propagate) throws Failure;
	/** 
	 * Perform substitute(y, x) for every binding x -> y in bindings.
	 * @throws Failure TODO
	 * 
	 */
	Constraint substitute(HashMap<C_Root, C_Var> bindings) throws Failure;
	Constraint substitute(HashMap<C_Root, C_Var> bindings, boolean propagate) throws Failure;
	/**
	 * Preconditions: x occurs in this.
	 * It must be the case that the real clause of the
	 * type of y, S, entails the real clause of the type of x, T.
	 * Assume that this and S are fully explicit, that is the
	 * consequences of the types U of variables v occuring in them
	 * have been added to them. 
	 * 
	 * Replace all occurrences of x in this by y. 
	 * For every binding y.p = t in the result, for every binding
	 * self.p.q = t1 in S add y.p.q=t1[y/self] to the result.
	 * Return this now fully explicit constraint.
	 * @param y
	 * @param x
	 * @throws Failure TODO
	 */
	void applySubstitution(C_Var y, C_Root x, boolean propagate) throws Failure;
	void applySubstitution(HashMap<C_Root, C_Var> bindings, boolean propagate) throws Failure;
	
	/**
	 * Return the constraint obtained by replacing each local variable
	 * with index i in this by the selfVar of the i'th element in li, if it has
	 * one, or with a gensym otherwise. Used when defining an instantiation of
	 * the return type of a methodinstance.
	 * @param li
	 * @return
	 * @throws Failure 
	 */
	Constraint instantiate(List<X10Type> li) throws Failure;
	
	/**
	 * Does this constraint contain occurrences of the variable v?
	 * @param v
	 * @return true iff v is a root variable of this.
	 */
	boolean hasVar(C_Root v);
	
	/**
	 * Return the C_Var corresponding to this arg, if it is rigid, otherwise
	 * an EQVar from this constraint.
	 * @param arg
	 * @param type
	 * @return
	 */
	C_Var selfVar(Expr arg);
	C_Var selfVar(Expr arg, boolean hidden);
	
	/**
	 * For each C_Var v in the root, propagate v. To propagate v is to lookup v's type,
	 * saturate it, and transfer these constraints into the current constraint.
	 * @throws Failure 
	 */
	void saturate() throws Failure;
	
	int eqvCount();
	boolean placeIsHere();
	boolean placePossiblyNull();
	C_Term placeTerm();
}
