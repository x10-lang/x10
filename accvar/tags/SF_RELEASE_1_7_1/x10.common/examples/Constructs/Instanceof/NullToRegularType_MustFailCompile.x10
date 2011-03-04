/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a null value is not an instance of a non nullable type
 * Issue: null is not an instance of a non nullable type.
 * @author vcave
 **/
public class NullToRegularType_MustFailCompile extends x10Test {
	 
	public boolean run() {
		// X10 forbid null value to non nullable type.
		return !(null instanceof X10DepTypeClassOne);
	}
	
	public static void main(String[] args) {
		new NullToRegularType_MustFailCompile().execute();
	}
}
 