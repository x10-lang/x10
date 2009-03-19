/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a boxed primitive is an instanceof a nullable of the same type.
 * @author vcave
 **/
public class ObjectToNullablePrimitive extends x10Test {
	 
	public boolean run() {
		// transformed to new BoxedInteger(3);
		x10.lang.Object primitive = 3;
		// Type to check is transformed to /*nullable*/BoxedInteger
		return (primitive instanceof nullable<int>);
	}
	
	public static void main(String[] args) {
		new ObjectToNullablePrimitive().execute();
	}
}
 