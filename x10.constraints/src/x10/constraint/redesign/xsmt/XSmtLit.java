package x10.constraint.redesign.xsmt;

import x10.constraint.redesign.XLit;

public class XSmtLit<T extends XSmtType, V> extends XSmtTerm<T> implements XLit<T, V> {
	private final V val;
	
	XSmtLit(T t, V val) {
		super(t);
		this.val = val; 
	}

	@Override
	public V val() {
		return val;
	}

	@Override
	public void print(XPrinter p) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
	    if (val == null)              return "null";
	    if (val instanceof String)    return "\"" + val.toString() + "\"";
	    if (val instanceof Character) return "'" + val.toString() + "'";
	    if (val instanceof Float)     return val.toString() + "F";
	    if (val instanceof Long)      return val.toString() + "L";
	    return val.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((val == null) ? 0 : val.hashCode());
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
		XSmtLit<?,?> other = (XSmtLit<?,?>) obj;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}
	

}
