package x10.constraint.smt;

import x10.constraint.XEQV;


public class XSmtEQV<T extends XSmtType> extends XSmtTerm<T> implements XEQV<T>{
	private static String EQV_PREFIX = "#eqv";
	private final int num; 
	
	public XSmtEQV(T type, int num) {
		super(type);
		this.num = num; 
	}
	
	@Override
	public String toString() {
		return EQV_PREFIX + num; 
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
		XSmtEQV<?> other = (XSmtEQV<?>) obj;
		if (num != other.num)
			return false;
		return true;
	}

	@Override
	public void print(XPrinter p) {
		// TODO
	}

	
}
