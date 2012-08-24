package x10.types.constraints.smt;

import x10.constraint.XDef;
import x10.constraint.XType;
import x10.constraint.smt.XSmtVar;
import x10.types.constraints.CLocal;

public class CSmtLocal<T extends XType, D extends XDef<T>> extends XSmtVar<T> implements CLocal<T, D> {
	private final D def; 
	private final String s; // just for documentation

	public CSmtLocal(D def, T type) {
		super(type, def.getName());
		this.def = def;
		this.s = null; 
	}

	public CSmtLocal(D def, String s, T type) {
		super(type, def.getName());
		this.def = def;
		this.s = s; 
	}

	
	CSmtLocal(CSmtLocal<T, D> other) {
		super(other);
		this.def = other.def;
		this.s = other.s; 
	}
	
	@Override
	public D def() {
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
		CSmtLocal<?,?> other = (CSmtLocal<?,?>) obj;
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
	public CSmtLocal<T,D> copy() {
		return new CSmtLocal<T,D>(this); 
	}
}
