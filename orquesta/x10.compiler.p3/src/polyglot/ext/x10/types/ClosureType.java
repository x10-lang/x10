/*
 * Created on Mar 1, 2007
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.Ref;
import polyglot.types.Type;
import x10.constraint.XConstraint;

/**
 * The type of a closure, representing the closure's signature (argument types,
 * return type and throwable exception types).
 * @author rfuhrer
 */
public interface ClosureType extends X10Type {
    /**
     * @return the type of value returned by an invocation of the closure. cannot be void.
     */
    Ref<? extends Type> returnType();
    
    /**
     * @return the list of formal argument types of the closure, in declaration order. may be empty.
     */
    List<Ref<? extends Type>> typeParameters();

    /**
     * @return the list of formal argument types of the closure, in declaration order. may be empty.
     */
    List<Ref<? extends Type>> argumentTypes();

    /**
     * @return the where clause for the closure.
     */
    Ref<? extends XConstraint> whereClause();
    
    /**
     * @return the list of exception types that this closure may throw from an invocation. may be empty.
     */
    List<Ref<? extends Type>> throwTypes();
    
    /**
     * Sets the formal argument types of this closure
     */
    void setTypeParameters(List<Ref<? extends Type>> typeParams);

    /**
     * Sets the formal argument types of this closure
     */
    void argumentTypes(List<Ref<? extends Type>> argTypes);

    /**
     * Sets the return type of this closure
     */
    void returnType(Ref<? extends Type> returnType);

    /**
     * Sets the where clause for the closure.
     */
    void setWhereClause(Ref<? extends XConstraint> whereClause);
    
    /**
     * Sets the throwable exception types of this closure
     */
    void throwTypes(List<Ref<? extends Type>> throwTypes);
}
