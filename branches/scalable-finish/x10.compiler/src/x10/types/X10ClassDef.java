/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types;

import java.util.List;

import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.FieldDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;


public interface X10ClassDef extends X10Def, ClassDef, X10MemberDef {
    /** Conjunction of the class invariant and property invariants. */
    CConstraint getRootClause();
    void setRootClause(Ref<CConstraint> c);
    void checkRealClause() throws SemanticException;

    /** The class invariant. */
    Ref<CConstraint> classInvariant();
    void setClassInvariant(Ref<CConstraint> classInvariant);

    Ref<TypeConstraint> typeBounds() ;
    void setTypeBounds(Ref<TypeConstraint> c) ;

    /** Properties defined in the class.  Subset of fields(). */
    List<X10FieldDef> properties();
    
    List<ParameterType.Variance> variances();
    List<ParameterType> typeParameters();
    void addTypeParameter(ParameterType p, ParameterType.Variance v);
    
    /** Add a member type to the class. */
    List<TypeDef> memberTypes();
    
    /** Add a member type to the class. */
    void addMemberType(TypeDef t);
    
    /**
     * Is this the class def for an X10 struct?
     * @return
     */
    boolean isStruct();
    /**
     * Is this the class def for an X10 function?
     */
    boolean isFunction();
}
