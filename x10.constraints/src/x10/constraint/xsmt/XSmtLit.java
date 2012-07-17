package x10.constraint.xsmt;

import x10.constraint.XLit;
import x10.constraint.XVar;
import x10.constraint.xsmt.SmtUtil.XSmtKind;

public class XSmtLit<T> extends XSmtVar implements XLit, SmtConstant<T> {
	private static final long serialVersionUID = 7729222351927529012L;
	public final T value; 
	
	public XSmtLit(T val) { this.value = val; }
	
	@Override
	public T getValue() {
		return value;
	}

	@Override
	public Object val() {
		return value;
	}

	@Override
	public XSmtKind getKind() {
		return XSmtKind.CONSTANT;
	}

	@Override
	public boolean hasVar(XVar v) {
		return equals(v); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		XSmtLit<?> other = (XSmtLit<?>) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String toString() {
	    if (value == null)              return "null";
	    if (value instanceof String)    return "\"" + value.toString() + "\"";
	    if (value instanceof Character) return "'" + value.toString() + "'";
	    if (value instanceof Float)     return value.toString() + "F";
	    if (value instanceof Long)      return value.toString() + "L";
	    return value.toString();
	}

	@Override
	public String toSmt2() {
		if (value == null)
			return SmtUtil.mangle("null");
		
		return value.toString(); 
	}

}
