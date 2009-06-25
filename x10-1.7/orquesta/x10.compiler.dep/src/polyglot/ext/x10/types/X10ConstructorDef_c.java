/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef_c;
import polyglot.types.ConstructorInstance_c;
import polyglot.types.Flags;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.util.TypedList;

/**
 * An X10ConstructorInstance_c varies from a ConstructorInstance_c only in that it
 * maintains a returnType. If an explicit returnType is not declared in the constructor
 * then the returnType is simply a noClause variant of the container.
 * @author vj
 *
 */
public class X10ConstructorDef_c extends ConstructorDef_c implements X10ConstructorDef {
    Ref<? extends ClassType> returnType;
    protected Ref<? extends Constraint> supClause;
    protected Ref<? extends Constraint> whereClause;

    public X10ConstructorDef_c(TypeSystem ts, Position pos,
            Ref<? extends ClassType> container,
            Flags flags, 
            Ref<? extends ClassType> returnType,
            List<Ref<? extends Type>> formalTypes, List<Ref<? extends Type>> excTypes) {
        super(ts, pos, container, flags, formalTypes, excTypes);
        this.returnType = returnType;
    }

    public X10ConstructorDef_c(TypeSystem ts, Position pos,
            Ref<? extends X10ClassType> container,
            Flags flags, List<Ref<? extends Type>> formalTypes, List<Ref<? extends Type>> excTypes) {
        this(ts, pos, container, flags, container, formalTypes, excTypes);
    }

    // BEGIN ANNOTATION MIXIN
    List<Ref<? extends X10ClassType>> annotations;

    public List<Ref<? extends X10ClassType>> defAnnotations() {
        return Collections.unmodifiableList(annotations);
    }
    
    public void setDefAnnotations(List<Ref<? extends X10ClassType>> annotations) {
        this.annotations = TypedList.<Ref<? extends X10ClassType>>copyAndCheck(annotations, Ref.class, true);
    }
    
    public List<X10ClassType> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }
    
    public List<X10ClassType> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }
    
    public List<X10ClassType> annotationsNamed(String fullName) {
        return X10TypeObjectMixin.annotationsNamed(this, fullName);
    }
    // END ANNOTATION MIXIN
    
    public Ref<? extends ClassType> returnType() {
        return this.returnType;
    }

    public void setReturnType(Ref<? extends ClassType> r) {
        this.returnType = r;
    }
	
    /** Constraint on superclass constructor call return type. */
    public Ref<? extends Constraint> supClause() {
        return supClause;
    }

    public void setSupClause(Ref<? extends Constraint> s) {
        this.supClause = s;
    }

    /** Constraint on formal parameters. */
    public Ref<? extends Constraint> whereClause() {
        return whereClause;
    }

    public void setWhereClause(Ref<? extends Constraint> s) {
        this.whereClause = s;
    }
	
    public String signature() {
        return ((returnType != null) ? returnType.toString() : container.toString())
        + "(" + X10TypeSystem_c.listToString(formalTypes) + 
        (whereClause != null ? ": " + whereClause.toString() : "") + ")";
    }
}
