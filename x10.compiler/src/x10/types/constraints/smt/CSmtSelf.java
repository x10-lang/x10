package x10.types.constraints.smt;

import polyglot.types.Type;
import x10.constraint.smt.XPrinter;
import x10.constraint.smt.XSmtVar;
import x10.types.constraints.CSelf;

public class CSmtSelf extends XSmtVar<Type> implements CSelf {
	private static final String SELF_VAR_PREFIX="self";
    private final int num;
    
	public CSmtSelf(Type t, int num) {
		super(t, SELF_VAR_PREFIX + num+t);
		this.num = num; 
	}
	
	public CSmtSelf(CSmtSelf other) {
		super(other); 
		this.num = other.num; 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + num;
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
		CSmtSelf other = (CSmtSelf) obj;
		if (num != other.num)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return SELF_VAR_PREFIX + num + ":" + type();
	}
	@Override
	public String toSmtString() {
		return SELF_VAR_PREFIX; 
	}
	
	@Override
	public CSmtSelf copy() {
		return new CSmtSelf(this); 
	}	

}
