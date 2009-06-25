package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.FieldDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import x10.constraint.XConstraint;

public interface X10ClassDef extends X10Def, ClassDef {
    /** Conjunction of the class invariant and property invariants. */
    XConstraint getRootClause();
    void checkRealClause() throws SemanticException;

    /** The class invariant. */
    Ref<XConstraint> classInvariant();
    void setXClassInvariant(Ref<XConstraint> classInvariant);
    
    /** Properties defined in the class.  Subset of fields(). */
    List<X10FieldDef> properties();
    
    List<TypeProperty.Variance> variances();
    List<ParameterType> typeParameters();
    void addTypeParameter(ParameterType p, TypeProperty.Variance v);
    
    List<TypeProperty> typeProperties();
    void addTypeProperty(TypeProperty p);
    
    /** Add a member type to the class. */
    List<TypeDef> memberTypes();
    
    /** Add a member type to the class. */
    void addMemberType(TypeDef t);
}
