package polyglot.ext.x10.types.constr;

import java.io.Serializable;
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
	boolean entails(C_Var var, C_Term val);
	C_Term find(String varName);
	/**
	 * Return the set of bindings in the constraint. null is retained if 
	 * there are no bindings.
	 * @return the set of bindings, null if there are none.
	 */
	Map bindings();
	/**
	 * Add a binding var=val to the constraint.
	 * @param var
	 * @param t
	 * @return new constraint with var=val added.
	 */
	Constraint addBinding(C_Var var, C_Term val);
	/**
	 * Add the binding term=true to the constraint.
	 * @param term -- must be of type Boolean.
	 * @return new constraint with term=true added.
	 * @throws SemanticException
	 */
	Constraint addTerm(C_Term term) throws SemanticException;
}
