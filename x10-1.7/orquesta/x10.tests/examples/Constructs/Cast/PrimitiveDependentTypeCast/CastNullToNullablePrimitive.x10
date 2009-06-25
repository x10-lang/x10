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

	public def run(): boolean = {
		// Expression type changes to /*nullable*/BoxedInteger
		var i: Box[int] = null as Box[int];
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastNullToNullablePrimitive().execute();
	}
}
