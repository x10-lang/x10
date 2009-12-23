/*
 * Created on Mar 1, 2007
 */
/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.ArrayList;
import java.util.List;

import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Ref;
import polyglot.types.Type;
import x10.constraint.XConstraint;

/**
 * The type of a closure, representing the closure's signature (argument types,
 * return type and throwable exception types).
 * @author rfuhrer
 */
public interface FunctionType extends X10ParsedClassType {
//    ClosureDef closureDef();
    
//    ClosureInstance closureInstance();
//    ClosureType closureInstance(ClosureInstance ci);
    
    X10MethodInstance applyMethod();

    /**
     * @return the type of value returned by an invocation of the closure. cannot be void.
     */
    Type returnType();

    /**
     * @return the list of formal type arguments of the closure, in declaration order. may be empty.
     * Note: This differs from typeArguments, inherited from X10ClassType.
     * For [S](T) => U, 
     * the class type for the closure is Fun_1_1[T,U].
     * typeParameters is [S] and argumentTypes is (T).  typeArguments is [T,U].
     */
    List<Type> typeParameters();

    /**
     * @return the list of formal value argument types of the closure, in declaration order. may be empty.
     */
    List<Type> argumentTypes();

    /**
     * @return the list of formal value argument names of the closure, in declaration order. may be empty.
     */
    List<LocalInstance> formalNames();
    
    /**
     * @return the guard for the closure.
     */
    XConstraint guard();

    /**
     * @return the list of exception types that this closure may throw from an invocation. may be empty.
     */
    List<Type> throwTypes();
    
//    public ClosureType returnType(Type l);
//
//    public ClosureType guard(XConstraint l);
//
//    public ClosureType typeParameters(List<Type> l);
//
//    public ClosureType argumentTypes(List<Type> l);
//
//    public ClosureType throwTypes(List<Type> l);
//
//    public List<Type> typeArguments();
}
