/*
 * Created on Mar 1, 2007
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.Type;

/**
 * The type of a closure, representing the closure's signature (argument types,
 * return type and throwable exception types).
 * @author rfuhrer
 */
public interface ClosureType extends Type {
    /**
     * @return the type of value returned by an invocation of the closure. cannot be void.
     */
    Type returnType();

    /**
     * @return the list of formal argument types of the closure, in declaration order. may be empty.
     */
    List<Type> argumentTypes();

    /**
     * @return the list of exception types that this closure may throw from an invocation. may be empty.
     */
    List<Type> throwTypes();

    /**
     * Sets the formal argument types of this closure
     */
    void argumentTypes(List<Type> argTypes);

    /**
     * Sets the return type of this closure
     */
    void returnType(Type returnType);

    /**
     * Sets the throwable exception types of this closure
     */
    void throwTypes(List<Type> throwTypes);
}
