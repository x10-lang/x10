/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a boxed primitive is an instanceof this same primitive type .
 * @author vcave
 **/
public class ObjectToPrimitive2 extends x10Test {
	 
	public boolean run() {
		X10DepTypeClassOne [] array = new X10DepTypeClassOne [1];
		x10.lang.Object var = array[0];
		return !(var instanceof int);
	}
	
	public static void main(String[] args) {
		new ObjectToPrimitive2().execute();
	}
}
 