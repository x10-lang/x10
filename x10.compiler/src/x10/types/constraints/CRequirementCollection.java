/**
 * 
 */
package x10.types.constraints;

import java.util.List;

import polyglot.types.Context;
import polyglot.types.Type;
import x10.constraint.XFailure;

/**
 * @author Louis Mandel
 * @author Olivier Tardieu
 *
 */
public interface CRequirementCollection {

	public List<CRequirement> requirements();

	public boolean isEmpty();

	/**
	 * Add a new requirement.
	 * 
     * Note: this is side-effected by this operation.
	 * 
	 * @param hyp -- Hypothesis in which the requirement must be satisfied.
	 * @param req -- Property that must be satisfied.
	 * @param ctx -- The context of the expression.
	 * @return
	 * @throws XFailure  -- raised if the requirement is inconsistent
	 */
	public void add(CConstraint hyp, CConstraint req, Context ctx) throws XFailure;

	/**
	 * Add a new requirement. The requirement that must be added such that the actual type implies the expected type.
	 * 
     * Note: this is side-effected by this operation.
     * 
	 * @param actual -- The type of the expression.
	 * @param expected -- The type which is expected.
	 * @param ctx -- The context of the expression.
	 * @return  
	 * @throws XFailure  -- raised if the requirement is inconsistent
	 */
	public void add(Type actual, Type expected, Context ctx) throws XFailure;


	/**
	 * Compute a constraint c which entails all the requirements h => r that are in the collection.
	 * That is such that c /\ h => r. The free variables of h and r with respect to ctx must not
	 * occur in c.
	 * 
	 * @param ctx Context which defines the binding.
	 * @return
	 * @throws XFailure Raised if the guard is inconsistent.
	 */
	public CConstraint makeGuard(Context ctx) throws XFailure;

}
