/**
 * 
 */
/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.LocalInstance;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import x10.constraint.XConstraint;

/**
 * @author vj
 *
 */
public interface X10ProcedureInstance<T extends ProcedureDef> extends X10TypeObject, ProcedureInstance<T> {
    // Constructors, methods, and closures all have return types.
    Type returnType();
    ProcedureInstance<T> returnType(Type t);
    Ref<? extends Type> returnTypeRef();
    ProcedureInstance<T> returnTypeRef(Ref<? extends Type> t);
    
    List<Type> typeParameters();
    X10ProcedureInstance<T> typeParameters(List<Type> typeParameters);
    
    List<LocalInstance> formalNames();
    X10ProcedureInstance<T> formalNames(List<LocalInstance> formalNames);
    
    /**
     * Return the constraint on the formal parameters, if any.
     * @return
     */
    XConstraint guard();
    X10ProcedureInstance<T> guard(XConstraint guard);
    
    /**
     * Return the constraint on the type parameters, if any.
     * @return
     */
    TypeConstraint typeGuard();
    X10ProcedureInstance<T> typeGuard(TypeConstraint guard);
}
