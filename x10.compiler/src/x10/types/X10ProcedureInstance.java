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

package x10.types;

import java.util.List;

import polyglot.types.LocalInstance;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import x10.types.constraints.CConstraint;


/**
 * @author vj
 *
 */
public interface X10ProcedureInstance<T extends ProcedureDef> extends TypeObject, ProcedureInstance<T> {
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
    CConstraint guard();
    X10ProcedureInstance<T> guard(CConstraint guard);
    
    /**
     * Return the constraint on the type parameters, if any.
     * @return
     */
    TypeConstraint typeGuard();
    X10ProcedureInstance<T> typeGuard(TypeConstraint guard);
}
