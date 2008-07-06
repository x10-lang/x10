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
    XConstraint getRootXClause();
    void checkRealClause() throws SemanticException;

    /** The class invariant. */
    Ref<XConstraint> xclassInvariant();
    void setXClassInvariant(Ref<XConstraint> classInvariant);
    
    /** Properties defined in the class.  Subset of fields(). */
    List<FieldDef> properties();
    
    List<TypeProperty> typeProperties();
    void addTypeProperty(TypeProperty p);
    
    /** Add a member type to the class. */
    List<TypeDef> memberTypes();
    
    /** Add a member type to the class. */
    void addMemberType(TypeDef t);
}
