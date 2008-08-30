package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.types.ClassDef;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ObjectType;
import polyglot.types.Ref;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

public class BoxType_c extends X10ParsedClassType_c implements BoxType {

    public BoxType_c(ClassDef def) {
	super(def);
    }

    public BoxType_c(TypeSystem ts, Position pos, Ref<? extends ClassDef> def) {
	super(ts, pos, def);
    }
    
    public Type arg() {
	return typeArguments().get(0);
    }
    
    private Type base() {
        X10TypeSystem ts = (X10TypeSystem) this.ts;
        return ts.expandMacros(arg());
    }

    @Override
    public Type superClass() {
	X10TypeSystem ts = (X10TypeSystem) this.ts;
	return ts.Ref();
    }
    
    @Override
    public List<Type> interfaces() {
	if (base() instanceof ObjectType)
	    return ((ObjectType) base()).interfaces();
	return Collections.EMPTY_LIST;
    }
    
    @Override
    public List<FieldInstance> fields() {
	if (base() instanceof StructType)
	    return ((StructType) base()).fields();
	return Collections.EMPTY_LIST;
    }
    
    @Override
    public List<MethodInstance> methods() {
	if (base() instanceof StructType)
	    return ((StructType) base()).methods();
	return Collections.EMPTY_LIST;
    }

    public List<FieldInstance> properties() {
        if (base() instanceof X10ClassType)
            return ((X10ClassType) base()).properties();
        return Collections.EMPTY_LIST;
    }

    public List<Type> typeProperties() {
        if (base() instanceof X10ClassType)
            return ((X10ClassType) base()).typeProperties();
        return Collections.EMPTY_LIST;
    }
}
