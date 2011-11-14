package polyglot.types;

import java.util.List;

import polyglot.util.Position;

public abstract class FunctionInstance_c<T extends FunctionDef> extends ProcedureInstance_c<T> implements FunctionInstance<T> {
    private static final long serialVersionUID = 7771092895217853763L;

    public FunctionInstance_c(TypeSystem ts, Position pos, Position errorPos, Ref<? extends T> def) {
        super(ts, pos, errorPos, def);
    }
    
    protected Ref<? extends Type> returnType;

    public FunctionInstance<T> returnType(Type returnType) {
        return returnTypeRef(Types.ref(returnType));
    }

    public FunctionInstance<T> returnTypeRef(Ref<? extends Type> returnType) {
	FunctionInstance_c<T> p = this.<FunctionInstance_c<T>>copyGeneric();
	p.returnType = returnType;
	return p;
    }
    
    public Type returnType() {
        if (returnType == null) {
            return def().returnType().get();
        }
        return Types.get(returnType);
    }
    
    public Ref<? extends Type> returnTypeRef() {
	if (returnType == null) {
	    return def().returnType();
	}
	return returnType;
    }

    public FunctionInstance<T> formalTypes(List<Type> formalTypes) {
        return (FunctionInstance<T>) super.formalTypes(formalTypes);
    }
    
    public FunctionInstance<T> throwTypes(List<Type> throwTypes) {
        return (FunctionInstance<T>) super.throwTypes(throwTypes);
    }

}
