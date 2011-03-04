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
public interface BindingConstraint extends SimpleConstraint {
	/**
	 * Is the consistent consistent? That is, does it have a solution?
	 * @return true iff the constraint is consistent.
	 */
	boolean consistent();
	void setInconsistent();

	HashMap<C_Var, Promise> roots();
	
	/**
	 * Return the term this variable is bound to in the constraint, and null if there is no such term.
	 * @param varName
	 * @return
	 */
	C_Var find(String varName);
	
	BindingConstraint copy();
	BindingConstraint clone();
	
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
	void internRecursively(C_Var variable, Constraint container) throws Failure;
	
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
	 * For each C_Var v in the root, propagate v. To propagate v is to lookup v's type,
	 * saturate it, and transfer these constraints into the current constraint.
	 * @param container TODO
	 * @throws Failure 
	 */
	void saturate(Constraint container) throws Failure;
	
	BindingConstraint substitute(C_Var y, C_Root x, boolean propagate, Constraint container, HashSet<C_Term> visited) throws Failure;
	boolean entailsBinding(C_Var key, C_Var value, Constraint me);
}
