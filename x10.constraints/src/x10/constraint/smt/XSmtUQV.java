package x10.constraint.smt;

import x10.constraint.XUQV;

public class XSmtUQV<T extends XSmtType> extends XSmtTerm<T> implements XUQV<T> {
	private static String EQV_PREFIX = "#uqv";
	private final int num; 
	private final String name;
	
	public XSmtUQV(T type, int num) {
		super(type);
		this.num = num;
		this.name = null;
	}
	public XSmtUQV(T type, String name, int num) {
		super(type);
		this.num = num;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name ==  null? EQV_PREFIX + num : name; 
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
		XSmtUQV<?> other = (XSmtUQV<?>) obj;
		if (num != other.num)
			return false;
		return true;
	}

	@Override
	public void print(XPrinter p) {
		// TODO
	}

}
