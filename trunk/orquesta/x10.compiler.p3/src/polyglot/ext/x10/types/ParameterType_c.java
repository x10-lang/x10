package polyglot.ext.x10.types;

import polyglot.types.Def;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.TypeObject;
import polyglot.types.Type_c;
import polyglot.util.Position;

public class ParameterType_c extends Type_c implements ParameterType {
	String name;
	Ref<? extends Def> def;
	
	public ParameterType_c(X10TypeSystem ts, Position pos, String name, Ref<? extends Def> def) {
		super(ts, pos);
		this.name = name;
		this.def = def;
	}
	
	public boolean isGloballyAccessible() {
	    return false;
	}
	
	public String fullName() {
		return name;
	}
	
	public String name() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public String translate(Resolver c) {
		return name;
	}

	public boolean safe() {
		return false;
	}
	
	@Override
	public boolean equalsImpl(TypeObject t) {
		if (t instanceof ParameterType) {
			ParameterType pt = (ParameterType) t;
			return def == pt.def() && name.equals(pt.name());
		}
		return false;
	}

	public Ref<? extends Def> def() {
		return def;
	}

	public ParameterType def(Ref<? extends Def> def) {
		ParameterType_c n = (ParameterType_c) copy();
		n.def = def;
		return n;
	}
}
