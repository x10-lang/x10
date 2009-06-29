/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.types.FieldDef_c;
import polyglot.types.Flags;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.Name;
import polyglot.types.StructType;
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
public class X10FieldDef_c extends FieldDef_c implements X10FieldDef {
    boolean isProperty;
    
    public X10FieldDef_c(TypeSystem ts, Position pos,
            Ref<? extends StructType> container,
            Flags flags, 
            Ref<? extends Type> type,
            Name name) {
        super(ts, pos, container, flags, type, name);
        isProperty = false;
    }

    // BEGIN ANNOTATION MIXIN
    List<Ref<? extends Type>> annotations;

    public List<Ref<? extends Type>> defAnnotations() {
	if (annotations == null) return Collections.EMPTY_LIST;
        return Collections.unmodifiableList(annotations);
    }
    
    public void setDefAnnotations(List<Ref<? extends Type>> annotations) {
        this.annotations = TypedList.<Ref<? extends Type>>copyAndCheck(annotations, Ref.class, true);
    }
    
    public List<Type> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }
    
    public List<Type> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }
    
    public List<Type> annotationsNamed(QName fullName) {
        return X10TypeObjectMixin.annotationsNamed(this, fullName);
    }
    // END ANNOTATION MIXIN
    
    public boolean isProperty() {
        return isProperty;
    }
    
    public void setProperty() {
        isProperty = true;        
    }
}
