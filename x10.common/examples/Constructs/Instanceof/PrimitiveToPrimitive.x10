/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks litteral primitive is an instance of the same type.
 * @author vcave
 **/
public class PrimitiveToPrimitive extends x10Test {
	 
	public boolean run() {
		//transformed to new BoxedInteger(3) instanceof
		return (3 instanceof int);
	}
	
	public static void main(String[] args) {
		new PrimitiveToPrimitive().execute();
	}
}
 