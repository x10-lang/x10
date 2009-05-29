/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.TypeSystem;

/**
 * @author vj
 *
 */
public interface X10ProcedureInstance extends X10TypeObject {
	boolean callValidImplNoClauses(List argTypes);
	
	List<X10Type> formalTypes();
	
	TypeSystem typeSystem();

	/** Return the constraint on the formal parameters, if any.
	 * @return
	 */
	Constraint whereClause();
	
	/** Set the constraint on the formal parameters.
	 * @param c
	 */
	void setWhereClause(Constraint c);
}
