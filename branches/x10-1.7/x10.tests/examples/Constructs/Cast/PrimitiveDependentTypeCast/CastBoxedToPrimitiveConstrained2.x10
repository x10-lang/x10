/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks unboxing works properly when dependent type occurs in the cast.
 * Issue: Unboxed object does not meet declared cast constraint.
 * @author vcave
 **/
 public class CastBoxedToPrimitiveConstrained2 extends x10Test {

	public def run(): boolean = {
		try {
			var obj: x10.lang.Object = 2;
			// At runtime, cast checking code detects unboxed object does
			// not meet constraint.
			var i: int = obj as int{self==3};
		} catch (e: ClassCastException) {
			return true;
		}
		return false;
	}

	public static def main(var args: Rail[String]): void = {
		new CastBoxedToPrimitiveConstrained2().execute();
	}
}
