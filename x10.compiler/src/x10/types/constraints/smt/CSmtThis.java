package x10.types.constraints.smt;

import polyglot.types.Type;
import x10.constraint.smt.XPrinter;
import x10.constraint.smt.XSmtVar;
import x10.types.constraints.CThis;

public class CSmtThis extends XSmtVar<Type> implements CThis {
	private static final String THIS_VAR_PREFIX="this";
    private final int num;

    public CSmtThis(Type t, int num) {
		super(t, THIS_VAR_PREFIX+num+t);
		this.num = num;
	}
    
    public CSmtThis(CSmtThis other) {
    	super(other); 
    	this.num = other.num;
    }
    
	@Override
	public String toString() {
		return THIS_VAR_PREFIX + ":" + type(); 
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
		CSmtThis other = (CSmtThis) obj;
		if (num != other.num)
			return false;
		return true;
	}
	@Override
	public String prettyPrint() {
		return THIS_VAR_PREFIX; 
	}
	
	@Override
	public CSmtThis copy() {
		return new CSmtThis(this); 
	}
}
