/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import polyglot.types.Def;
import polyglot.types.Flags;
import polyglot.types.Named;
import polyglot.types.ProcedureDef;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.Name;
import polyglot.types.TypeObject;
import polyglot.types.Type_c;
import polyglot.util.InternalCompilerError;
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
	    return name.toString();
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
	 // begin Flagged mixin
    public Flags flags() { return Flags.NONE;}
    public X10Type setFlags(Flags flags) { 
    	assert false : "Canot set flags on ParameterType_c";
    	throw new InternalCompilerError("Cannot set flags on " + this);
    }
    public X10Type clearFlags(Flags flags) { 
    	return this;
    }
    
    public boolean isRooted() { return false; }
    public boolean isX10Struct() { return false; }
    // end Flagged mixin
    public boolean equalsNoFlag(X10Type t2) {
		return this == t2;
	}
}
