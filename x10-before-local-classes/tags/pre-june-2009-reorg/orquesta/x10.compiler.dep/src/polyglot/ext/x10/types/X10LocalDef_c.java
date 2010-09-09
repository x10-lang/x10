/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.types.Flags;
import polyglot.types.LocalDef_c;
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
public class X10LocalDef_c extends LocalDef_c implements X10LocalDef {
    int positionInArgList;
    
    public X10LocalDef_c(TypeSystem ts, Position pos,
            Flags flags, 
            Ref<? extends Type> type,
            String name) {
        super(ts, pos, flags, type, name);
        positionInArgList = -1;
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
    
    public int positionInArgList() {
        return positionInArgList;
    }
    public void setPositionInArgList(int i) {
        positionInArgList = i;
    }

}
