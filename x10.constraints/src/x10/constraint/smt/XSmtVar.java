package x10.constraint.smt;

import x10.constraint.XType;
import x10.constraint.XVar;

public abstract class XSmtVar<T extends XType> extends XSmtTerm<T> implements XVar<T> {
	private String name; 
	
	public XSmtVar(T t, String name) {
		super(t);
		this.name = name; 
	}
	
	public XSmtVar(XSmtVar<T> other) {
		super(other);
		this.name = other.name; 
	}

	@Override
	public boolean hasVar(XVar<T> var) {
		return equals(var);
	}
	

	@Override
	public String name() {
		return name; 
	}

	@Override
	protected void print(XSmtConstraintSystem<T> cs, XPrinter<T> p) {
		p.append(p.mangle(toString()));
	}
	
	@Override
	protected void declare(XSmtConstraintSystem<T> cs, XPrinter<T> p) {
		p.declare(cs, this); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		if (! (obj instanceof XSmtVar))
			return false;
		
		@SuppressWarnings("unchecked")
		XSmtVar<T> other = (XSmtVar<T>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name; // + ":" + type().typetoSmtString(); 
	}
	
	@Override
	public String toSmtString() {
		return name; 
	}


}
