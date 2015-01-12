/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.types;

import java.util.List;

import polyglot.types.Def;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Type;
import x10.types.constraints.TypeConstraint;

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
    List<Type> annotationsNamed(QName fullName);
    
    /**
     * Return the type guard, if any, associated with this definition.
     * The type guard specifies the type constraints on type parameters
     * introduced by this definition.
     * @return
     */
    Ref<TypeConstraint> typeGuard();
}
