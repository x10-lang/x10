package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.ClassDef;
import polyglot.types.FieldDef;
import polyglot.types.Ref;
import polyglot.types.Type;

public interface X10ClassDef extends X10Def, ClassDef {
    /** Conjunction of the class invariant and property invariants. */
    Constraint getRootClause();

    /** The class invariant. */
    Ref<? extends Constraint> classInvariant();
    void setClassInvariant(Ref<? extends Constraint> classInvariant);
    
    /** Properties defined in the class.  Subset of fields(). */
    List<FieldDef> properties();
}
