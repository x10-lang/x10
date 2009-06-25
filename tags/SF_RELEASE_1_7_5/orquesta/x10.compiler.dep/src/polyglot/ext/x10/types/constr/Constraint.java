/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Unary;
import polyglot.ext.x10.types.X10Type;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;

/**
 * An implementation of a simple constraint system. The only constraints expressible are
 * x=a, where x is a variable and a is a constant. In particular, variable=variable 
 * constraints are not expressible.
 * 
 * A null constraint is treated as true (valid).
 * @author vj
 *
 */
public interface Constraint extends TypeObject {
    List<SimpleConstraint> conjuncts();
    
	/**
	 * Is the consistent consistent? That is, does it have a solution?
	 * @return true iff the constraint is consistent.
	 */
	boolean consistent();
	void setInconsistent();
	
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
	 * @throws Failure TODO
	 */
	void addIn(Constraint c) throws Failure;
        void addIn(SimpleConstraint c) throws Failure;

	/**
	 * Add the binding term=true to the constraint.
	 * @param term -- must be of type Boolean.
	 * @throws SemanticException
	 */
	void addTerm(C_Term term) throws Failure;
	C_Var selfVar();
	void setSelfVar(C_Var val);
	
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
	Constraint substitute(C_Var y, C_Root x, boolean propagate, HashSet<C_Term> visited) throws Failure;
	/** 
	 * xs and ys must be of the same length.
	 * Perform substitute(ys[i], xs[i]) for each i < xs.length.
	 * @throws Failure TODO
	 */
	Constraint substitute(C_Var[] ys, C_Root[] xs) throws Failure;
	Constraint substitute(C_Var[] ys, C_Root[] xs, boolean propagate, HashSet<C_Term> visited) throws Failure;
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
	 * @param visited TODO
	 * @throws Failure TODO
	 */
	void applySubstitution(C_Var y, C_Root x, boolean propagate, HashSet<C_Term> visited) throws Failure;
	void applySubstitution(HashMap<C_Root, C_Var> bindings, boolean propagate) throws Failure;
	
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

    Constraint unaryOp(Unary.Operator op);
    Constraint binaryOp(Binary.Operator op, Constraint c);

    Set<C_EQV> eqvs();

    void addSelfBinding(C_Var val) throws Failure;

    void internRecursively(C_Var var) throws Failure;
}
