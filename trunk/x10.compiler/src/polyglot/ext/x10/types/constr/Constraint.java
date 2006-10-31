package polyglot.ext.x10.types.constr;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import polyglot.types.SemanticException;

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
	boolean entails(C_Term var, C_Term val);
	
	/**
	 * Return the term this variable is bound to in the constraint, and null if there is no such term.
	 * @param varName
	 * @return
	 */
	C_Term find(String varName);
	
	/**
	 * Add t1 -> t2 to the constraint.
	 * @param var
	 * @param t
	 * @return new constraint with t1=t2 added.
	 */
	Constraint addBinding(C_Term var, C_Term val);
	/**
	 * For each pair (t1,t2) in result, ddd t1 -> t2 to the constraint,
	 * and return the resulting constraint.
	 * @param var
	 * @param t
	 * @return new constraint with t1=t2 added.
	 */
	Constraint addBindings(HashMap/*<C_Term,C_Term>*/ result);
	
	Constraint copy();
	/**
	 * Add constraint c into this, and return this.
	 * @param c
	 * @return
	 */
	Constraint addIn(Constraint c);
	/**
	 * Add the binding term=true to the constraint.
	 * @param term -- must be of type Boolean.
	 * @return new constraint with term=true added.
	 * @throws SemanticException
	 */
	Constraint addTerm(C_Term term) throws Failure;
	C_Var varWhoseTypeIsThis();
	void setVarWhoseTypeThisIs(C_Var val);
	
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
	Promise intern(C_Term term);
	
	/** Return the promise obtained by looking this term up in the constraint graph, and null
	 * if this term does not exist in the graph. This will never result in new promises
	 * being added to the graph.
	 * term: Literal -- return the literal.
	 * term: LocalVariable, Special, Here Check if term is already in the roots maintained by
	 * the constraint. If so, return the root, if not return null;
	 * term: C_Field. Start with the rootVar x and follow the path f1...fk, if term=x.f1...fk.
	 * If the graph contains no nodes after fi, for some i < k, return null. Otherwise return
	 * the last promise.
	 * @param term
	 * @return
	 */
	Promise lookup(C_Term term);
	
	/**
	 * Return in HashMap a set of bindings t1-> t2 equivalent to the current constraint.
	 * Equivalent to constraints(new HashMap()).
	 * @return
	 */
	HashMap/*<C_Term, C_Term>*/ constraints();
	/**
	 * Return result, after adding to it a set of bindings t1-> t2 equivalent to the 
	 * current constraint. 
	 * Equivalent to constraints(result, null, null).
	 * @return
	 */
	HashMap/*<C_Term, C_Term>*/ constraints(HashMap result);
	/**
	 * Return result, after adding to it a set of bindings t1-> t2 equivalent to the 
	 * current constraint, with all occurrences of self in t1 and t2 replaced by
	 * newSelf (provided that newSelf !=null), and all occurrences of this in t1 and t2
	 * replaced by newThis (provided that newThis !=null).
	 * @return
	 */
	HashMap/*<C_Term, C_Term>*/ constraints(HashMap result, C_Term newSelf, C_Term newThis);
	
}
