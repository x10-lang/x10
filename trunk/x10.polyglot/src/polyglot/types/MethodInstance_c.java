package polyglot.types;

import java.util.*;

import polyglot.main.Report;
import polyglot.util.Position;

public class MethodInstance_c extends FunctionInstance_c<MethodDef> implements MethodInstance {

    public MethodInstance_c(TypeSystem ts, Position pos, Ref<? extends MethodDef> def) {
        super(ts, pos, def);
    }
    
    protected Name name;
    protected Flags flags;
    protected StructType container;
    
    public MethodInstance container(StructType container) {
        MethodInstance_c p = (MethodInstance_c) copy();
        p.container = container;
        return p;
    }

    public StructType container() {
        if (this.container == null) {
            return Types.get(def().container());
        }
        return this.container;
    }
    
    public MethodInstance flags(Flags flags) {
        MethodInstance_c p = (MethodInstance_c) copy();
        p.flags = flags;
        return p;
    }
    
    public Flags flags() {
        if (this.flags == null) { 
            return def().flags();
        }
        return this.flags;
    }
    
    public MethodInstance name(Name name) {
        MethodInstance_c p = (MethodInstance_c) copy();
        p.name = name;
        return p;
    }

    public Name name() {
        if (this.name == null) { 
            return def().name();
        }
        return this.name;
    }
    
    public MethodInstance returnType(Type returnType) {
        return (MethodInstance) super.returnType(returnType);
    }
    public MethodInstance returnTypeRef(Ref<? extends Type> returnType) {
	return (MethodInstance) super.returnTypeRef(returnType);
    }

    public MethodInstance formalTypes(List<Type> formalTypes) {
        return (MethodInstance) super.formalTypes(formalTypes);
    }
    
    public MethodInstance throwTypes(List<Type> throwTypes) {
        return (MethodInstance) super.throwTypes(throwTypes);
    }
    
    /** Returns true iff <this> is the same method as <m> */
    public final boolean isSameMethod(MethodInstance m, Context context) {
	return ts.isSameMethod(this, m, context);
    }

    public final List<MethodInstance> overrides(Context context) {
	return ts.overrides(this, context);
    }
    
    @Override
    public String signature() {
	return name + super.signature();
    }

    /**
     * Leave this method in for historic reasons, to make sure that extensions
     * modify their code correctly.
     */
    public final boolean canOverride(MethodInstance mj, Context context) {
	return ts.canOverride(this, mj, context);
    }

    public final void checkOverride(MethodInstance mj, Context context) throws SemanticException {
	ts.checkOverride(this, mj, context);
    }

    public final List<MethodInstance> implemented(Context context) {
        return ts.implemented(this, context);
    }
}
