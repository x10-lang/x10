/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

/**
 * @author vj
 *
 */
public interface X10ProcedureInstance<T extends ProcedureDef> extends X10TypeObject, ProcedureInstance<T> {
    boolean callValidNoClauses(List<Type> argTypes);

    /**
     * Return the constraint on the formal parameters, if any.
     * @return
     */
    Constraint whereClause();

    X10ProcedureInstance<T> whereClause(Constraint where);

}
