/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class PrimitiveToNullablePrimitiveConstrained4 extends x10Test {
	 
	public boolean run() {
		int a = 3;
		return !(a instanceof nullable<int(:self==4)>);
	}
	
	public static void main(String[] args) {
		new PrimitiveToNullablePrimitiveConstrained4().execute();
	}
}
 