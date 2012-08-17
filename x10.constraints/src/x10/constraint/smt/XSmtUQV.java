package x10.constraint.smt;

import x10.constraint.XType;
import x10.constraint.XUQV;

public class XSmtUQV<T extends XType> extends XSmtVar<T> implements XUQV<T> {
	private static String UQV_PREFIX = "#uqv";
	private final int num; 
	private final String uqvName;
	
	public XSmtUQV(T type, int num) {
		super(type, UQV_PREFIX+num);
		if (num == 7417) {
			System.out.println("found");
		}
		this.num = num;
		this.uqvName = null;
	}
	public XSmtUQV(T type, String name, int num) {
		super(type, name + num);
		this.num = num;
		this.uqvName = name;
	}
	
	public XSmtUQV(XSmtUQV<T> other) {
		super(other);
		this.num = other.num;
		this.uqvName = other.uqvName; 
	}
	
	@Override
	public String prettyPrint() {
		return uqvName == null? UQV_PREFIX+ num : uqvName; 
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + num;
//		return result;
//	}

//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		XSmtUQV<?> other = (XSmtUQV<?>) obj;
//		if (num != other.num)
//			return false;
//		return true;
//	}

}
