/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.List;

import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.FieldDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import x10.constraint.XConstraint;

public interface X10ClassDef extends X10Def, ClassDef, X10MemberDef {
    /** Conjunction of the class invariant and property invariants. */
    XConstraint getRootClause();
    void setRootClause(Ref<XConstraint> c);
    void checkRealClause() throws SemanticException;

    /** The class invariant. */
    Ref<XConstraint> classInvariant();
    void setClassInvariant(Ref<XConstraint> classInvariant);

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
