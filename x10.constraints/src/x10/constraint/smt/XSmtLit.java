package x10.constraint.smt;

import x10.constraint.XLit;
import x10.constraint.XType;
import x10.constraint.XVar;

public class XSmtLit<T extends XType, V> extends XSmtTerm<T> implements XLit<T, V> {
	private final V val;
	
	protected XSmtLit(T t, V val) {
		super(t);
		this.val = val; 
	}
	
	XSmtLit(XSmtLit<T, V> other) {
		super(other);
		this.val = other.val; 
	}

	@Override
	public V val() {
		return val;
	}

	@Override
	public void print(XSmtConstraintSystem<T> cs, XPrinter<T> p) {
		if (val == null) {
			// we treat null as a variable that the printer keeps track of
			XSmtVar<T> var = p.nullVar(cs, type().<T>xTypeSystem());
			var.print(cs, p);
		} 
		else if (val instanceof Number) {
			String str = val.toString();
			// we need to output the decimal digit
			if (type().isFloat() || type().isDouble()) {
				if (!str.contains(".")) 
					str = str + ".0"; 
			}
			if (str.startsWith("-")) {
				str = "(- " + str.substring(1) + ")";
			}
			p.append(str);
		}
		else if (val instanceof String){
			// we treat string constants as fresh variables 
			XSmtVar<T> var = p.stringVar(cs, val.toString(), type());
			var.print(cs, p);
		} else {
			p.append(val.toString());
		}
	}
	
	@Override
	protected void declare(XSmtConstraintSystem<T> cs, XPrinter<T> p) {
		p.declare(cs, this); 
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

	@Override
	public boolean hasVar(XVar<T> var) {
		return false;
	}

	@Override
	public XSmtLit<T,V> copy() {
		return new XSmtLit<T,V>(this); 
	}

	@Override
	public String toSmtString() {
		if (val == null)
			return "null";
		return val.toString(); 
	}

}
