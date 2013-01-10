package x10.types.constraints.smt;

import polyglot.types.Def;
import polyglot.types.Type;
import x10.constraint.XDef;
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.smt.XSmtVar;
import x10.types.X10LocalDef;
import x10.types.constraints.CLocal;

public class CSmtLocal extends XSmtVar<Type> implements CLocal {
	private final X10LocalDef def; 
	private final String s; // just for documentation

	public CSmtLocal(X10LocalDef def, Type type) {
		super(type, def.getName());
		this.def = def;
		this.s = null; 
	}

	public CSmtLocal(X10LocalDef def, String s, Type type) {
		super(type, def.getName());
		this.def = def;
		this.s = s; 
	}

	
	CSmtLocal(CSmtLocal other) {
		super(other);
		this.def = other.def;
		this.s = other.s; 
	}
	
	@Override
	public X10LocalDef def() {
		return def; 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((def == null) ? 0 : def.getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CSmtLocal other = (CSmtLocal) obj;
		if (def == null) {
			if (other.def != null)
				return false;
		} else if (!def.getName().equals(other.def.getName()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return s == null? def.getName() : s;
	}
	
	@Override
	public CSmtLocal copy() {
		return new CSmtLocal(this); 
	}

}
