package polyglot.ext.x10.types;

import polyglot.types.Def;
import polyglot.types.Named;
import polyglot.types.ProcedureDef;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.Name;
import polyglot.types.TypeObject;
import polyglot.types.Type_c;
import polyglot.util.Position;

public class ParameterType_c extends Type_c implements ParameterType {
    Name name;
	Ref<? extends Def> def;
	
	public ParameterType_c(X10TypeSystem ts, Position pos, Name name, Ref<? extends Def> def) {
		super(ts, pos);
		this.name = name;
		this.def = def;
	}
	
	public boolean isGloballyAccessible() {
	    return false;
	}
	
	public QName fullName() {
		return QName.make(null, name);
	}
	
	public Name name() {
		return name;
	}
	
	@Override
	public String toString() {
	    String old = null;
	    if (def != null && def.getCached() instanceof Named) {
		old = ((Named) def.getCached()).toString();
	    }
	    return name + (old == null ? "" : " (in " + old + ")");
	}

	@Override
	public String translate(Resolver c) {
		return name.toString();
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
