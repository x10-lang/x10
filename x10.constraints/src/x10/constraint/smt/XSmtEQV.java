package x10.constraint.smt;

import x10.constraint.XEQV;
import x10.constraint.XType;


public class XSmtEQV<T extends XType> extends XSmtVar<T> implements XEQV<T>{
	private static String EQV_PREFIX = "#eqv";
	private final int num; 
	
	public XSmtEQV(T type, int num) {
		super(type, EQV_PREFIX + num);
		this.num = num;
		if (num == 10173) {
			System.out.println("eqv");
			//assert false; 
		}
	}
	
	public XSmtEQV(XSmtEQV<T> other) {
		super(other);
		this.num = other.num; 
	}
	
	@Override
	public String toSmtString() {
		return EQV_PREFIX+num; 
	}
	
	@Override
	public XSmtEQV<T> copy() {
		return new XSmtEQV<T>(this); 
	}

}
