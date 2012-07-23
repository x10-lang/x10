package x10.constraint.smt;

import x10.constraint.XDef;
import x10.constraint.XLocal;

public class XSmtLocal<T extends XSmtType, D extends XDef<T>> extends XSmtTerm<T> implements XLocal<T, D> {
	private final D def; 

	XSmtLocal(D def) {
		super(def.resultType());
		this.def = def;
	}
	
	@Override
	public D def() {
		return def; 
	}

	@Override
	public void print(XPrinter p) {
		// TODO Auto-generated method stub
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((def == null) ? 0 : def.hashCode());
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
		} else if (!def.equals(other.def))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return def.toString();
	}
	
	
}
