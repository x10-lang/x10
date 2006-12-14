/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks boxed integer value is checks against primtive dependent type.
 * @author vcave
 **/
public class ObjectToPrimitiveConstrained1 extends x10Test {
	 
	public boolean run() {
		x10.lang.Object primitive = 3;
		return (primitive instanceof int(:self==3));
	}
	
	public static void main(String[] args) {
		new ObjectToPrimitiveConstrained1().execute();
	}
}
 