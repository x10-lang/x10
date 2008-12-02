package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.frontend.GoalSet;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.Def_c;
import polyglot.types.DerefTransform;
import polyglot.types.MethodInstance;
import polyglot.types.Ref;
import polyglot.types.Symbol;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;

public class ClosureDef_c extends Def_c implements ClosureDef {

    // ### should be CodeInstance
    protected Ref<? extends CodeInstance<?>> methodContainer;
    protected Ref<? extends ClassType> typeContainer;
    Ref<? extends Type> returnType;
    protected List<Ref<? extends Type>> formalTypes;
    protected List<Ref<? extends Type>> throwTypes;
    protected CodeInstance<?> asInstance;

    public ClosureDef_c(TypeSystem ts, Position pos, 
            Ref<? extends ClassType> typeContainer,
            Ref<? extends CodeInstance<?>> methodContainer,
            Ref<? extends Type> returnType,
            List<Ref<? extends Type>> formalTypes, List<Ref<? extends Type>> throwTypes) {

        super(ts, pos);
        this.typeContainer = typeContainer;
        this.methodContainer = methodContainer;
        this.returnType = returnType;
        this.formalTypes = TypedList.copyAndCheck(formalTypes, Ref.class, true);
        this.throwTypes = TypedList.copyAndCheck(throwTypes, Ref.class, true);
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

    public CodeInstance<?> asInstance() {
        if (asInstance == null) {
            asInstance = ((X10TypeSystem) ts).createClosureInstance(position(), Types.ref(this));
        }
        return asInstance;
    }
    
    public Ref<? extends ClassType> typeContainer() {
        return typeContainer;
    }

    /**
     * @param container The container to set.
     */
    public void setTypeContainer(Ref<? extends ClassType> container) {
        this.typeContainer = container;
    }

    public Ref<? extends CodeInstance<?>> methodContainer() {
        return methodContainer;
    }

    public void setMethodContainer(Ref<? extends CodeInstance<?>> container) {
        methodContainer= container;
    }

    public Ref<? extends Type> returnType() {
        return returnType;
    }

    public void setReturnType(Ref<? extends Type> returnType) {
        assert returnType != null;
        this.returnType = returnType;
    }


    public List<Ref<? extends Type>> formalTypes() {
        return Collections.unmodifiableList(formalTypes);
    }

    /**
     * @param formalTypes The formalTypes to set.
     */
     public void setFormalTypes(List<Ref<? extends Type>> formalTypes) {
         this.formalTypes = TypedList.copyAndCheck(formalTypes, Ref.class, true);
     }

     public List<Ref<? extends Type>> throwTypes() {
         return Collections.unmodifiableList(throwTypes);
     }

     /**
      * @param throwTypes The throwTypes to set.
      */
     public void setThrowTypes(List<Ref<? extends Type>> throwTypes) {
         this.throwTypes = TypedList.copyAndCheck(throwTypes, Ref.class, true);
     }

     public String signature() {
         return "(" + X10TypeSystem_c.listToString(formalTypes) + ")";
     }

     public String designator() {
         return "closure";
     }

     public String toString() {
         return designator() + " " + this.returnType() + " " + signature();
     }

     public void setSymbol(Symbol<? extends TypeObject> sym) {
         // TODO Auto-generated method stub

     }

     public <T extends TypeObject> Symbol<T> symbol() {
         // TODO Auto-generated method stub
         return null;
     }

     public void complete(GoalSet phase) {
         // TODO Auto-generated method stub

     }
}
