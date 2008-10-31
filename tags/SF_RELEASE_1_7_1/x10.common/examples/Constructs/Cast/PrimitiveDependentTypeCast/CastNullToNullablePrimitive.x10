/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks cast if litteral null to primitive nullable works.
 * Note: The compiler drops the cast operation, as no checking is needed.
 * @author vcave
 **/
 public class CastNullToNullablePrimitive extends x10Test {

	public boolean run() {
		// Expression type changes to /*nullable*/BoxedInteger
		nullable<int> i = (nullable<int>) null;
		return true;
	}

	public static void main(String[] args) {
		new CastNullToNullablePrimitive().execute();
	}
}