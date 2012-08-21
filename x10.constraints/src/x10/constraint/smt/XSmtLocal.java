package x10.constraint.smt;

import x10.constraint.XDef;
import x10.constraint.XLocal;
import x10.constraint.XType;

public class XSmtLocal<T extends XType, D extends XDef<T>> extends XSmtVar<T> implements XLocal<T, D> {
	private final D def; 
	private final String s; // just for documentation

	public XSmtLocal(D def, T type) {
		super(type, def.getName());
		this.def = def;
		this.s = null; 
	}

	public XSmtLocal(D def, String s, T type) {
		super(type, def.getName());
		this.def = def;
		this.s = s; 
	}

	
	XSmtLocal(XSmtLocal<T, D> other) {
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
		XSmtLocal<?,?> other = (XSmtLocal<?,?>) obj;
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
	public XSmtLocal<T,D> copy() {
		return new XSmtLocal<T,D>(this); 
	}
}
