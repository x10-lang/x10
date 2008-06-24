package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.Def_c;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.constraint.XConstraint;

public class ClosureDef_c extends Def_c implements ClosureDef {

    protected Ref<? extends CodeInstance<?>> methodContainer;
    protected Ref<? extends ClassType> typeContainer;
    protected Ref<? extends Type> returnType;
    protected List<Ref<? extends Type>> typeParameters;
    protected List<Ref<? extends Type>> formalTypes;
    protected List<Ref<? extends Type>> throwTypes;
    protected Ref<? extends XConstraint> whereClause;
    protected CodeInstance<?> asInstance;

    public ClosureDef_c(TypeSystem ts, Position pos, 
            Ref<? extends ClassType> typeContainer,
            Ref<? extends CodeInstance<?>> methodContainer,
            Ref<? extends Type> returnType,
            List<Ref<? extends Type>> typeParams,
            List<Ref<? extends Type>> formalTypes,
            Ref<? extends XConstraint> whereClause,
            List<Ref<? extends Type>> throwTypes) {

        super(ts, pos);
        this.typeContainer = typeContainer;
        this.methodContainer = methodContainer;
        this.returnType = returnType;
        this.typeParameters = TypedList.copyAndCheck(typeParams, Ref.class, true);
        this.formalTypes = TypedList.copyAndCheck(formalTypes, Ref.class, true);
        this.whereClause = whereClause;
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

    public List<Ref<? extends Type>> typeParameters() {
	        return Collections.unmodifiableList(typeParameters);
    }

    public void setTypeParameters(List<Ref<? extends Type>> typeParameters) {
	    this.typeParameters = TypedList.copyAndCheck(typeParameters, Ref.class, true);
    }

    public Ref<? extends XConstraint> whereClause() {
	    return whereClause;
    }
    
    public void setWhereClause(Ref<? extends XConstraint> s) {
	    this.whereClause = s;
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
         return "(" + CollectionUtil.listToString(formalTypes) + ")";
     }

     public String designator() {
         return "closure";
     }

     public String toString() {
         return designator() + " " + this.returnType() + " " + signature();
     }
}
