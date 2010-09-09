package polyglot.types;

import java.util.List;

import polyglot.util.Position;

public class FunctionInstance_c<T extends FunctionDef> extends ProcedureInstance_c<T> implements FunctionInstance<T> {
    public FunctionInstance_c(TypeSystem ts, Position pos, Ref<? extends T> def) {
        super(ts, pos, def);
    }
    
    protected Ref<? extends Type> returnType;

    public FunctionInstance<T> returnType(Type returnType) {
        return returnTypeRef(Types.ref(returnType));
    }

    public FunctionInstance<T> returnTypeRef(Ref<? extends Type> returnType) {
	FunctionInstance_c<T> p = (FunctionInstance_c<T>) copy();
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
