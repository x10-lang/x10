package polyglot.types;

import java.util.List;

import polyglot.util.Position;

public class ConstructorInstance_c extends ProcedureInstance_c<ConstructorDef> implements ConstructorInstance {
    public ConstructorInstance_c(TypeSystem ts, Position pos, Ref<? extends ConstructorDef> def) {
        super(ts, pos, def);
    }

    protected Flags flags;
    protected StructType container;
    
    public ConstructorInstance container(StructType container) {
        ConstructorInstance_c p = (ConstructorInstance_c) copy();
        p.container = container;
        return p;
    }

    public StructType container() {
        if (this.container == null) {
            return Types.get(def().container());
        }
        return this.container;
    }
    
    public ConstructorInstance flags(Flags flags) {
        ConstructorInstance_c p = (ConstructorInstance_c) copy();
        p.flags = flags;
        return p;
    }

    public Flags flags() {
        if (this.flags == null) { 
            return def().flags();
        }
        return this.flags;
    }
    
    public ConstructorInstance formalTypes(List<Type> formalTypes) {
        return (ConstructorInstance) super.formalTypes(formalTypes);
    }
    
    public ConstructorInstance throwTypes(List<Type> throwTypes) {
        return (ConstructorInstance) super.throwTypes(throwTypes);
    }
    
    public ConstructorInstance instantiate(ClassType objectType,
    		List<Type> argumentTypes) throws SemanticException {
    	return this;
    }
}
