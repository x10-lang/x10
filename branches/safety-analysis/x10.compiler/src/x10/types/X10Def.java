/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.List;

import polyglot.types.Def;
import polyglot.types.Ref;
import polyglot.types.Type;

/**
 * Commentary by vj.
 * Interface implemented by all X10 definitions:
 * 
 * TypeDef  -- a type definition in X10 (macro-expander)
 * X10ProcedureDef
 * X10ConstructorDef
 * X10MethodDef
 * ClosureDef
 * X10ClassDef
 * X10FieldDef
 * X10LocalDef
 * X10MemberDef
 * X10InitializerDef
 * 
 *   
 * @author njnystrom
 * @author vj
 *
 */
public interface X10Def extends Def {
    /** Get the annotations on the definition. */
    List<Ref<? extends Type>> defAnnotations();
    
    /** Set the annotations on the definition. */
    void setDefAnnotations(List<Ref<? extends Type>> ats);

    List<Type> annotations();
    List<Type> annotationsMatching(Type t);
    
    /**
     * Return the type guard, if any, associated with this definition.
     * The type guard specifies the type constraints on type parameters
     * introduced by this definition.
     * @return
     */
    Ref<TypeConstraint> typeGuard();
}
