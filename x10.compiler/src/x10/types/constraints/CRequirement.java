/**
 * 
 */
package x10.types.constraints;

import polyglot.types.Context;
import polyglot.types.Type;
import x10.constraint.XFailure;
import x10.constraint.XVar;

/**
 * @author Louis Mandel
 * @author Olivier Tardieu
 *
 */
public interface CRequirement {

	/**
	 * The context in which the requirement must be satisfied.
	 */
	public CConstraint hypothesis();

	/**
	 * The requirement to satisfy.
	 */
	public CConstraint require();

}
