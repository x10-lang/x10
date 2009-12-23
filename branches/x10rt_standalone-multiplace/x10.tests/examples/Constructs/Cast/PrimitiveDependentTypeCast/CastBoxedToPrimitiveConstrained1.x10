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
 * @author vcave
 **/
 public class CastBoxedToPrimitiveConstrained1 extends x10Test {

	public def run(): boolean = {
		var obj: x10.lang.Object = 3;
		// an additionnal check is needed to ensure unboxed primitive meet constraints.
		var i: int = obj as int{self==3};
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastBoxedToPrimitiveConstrained1().execute();
	}
}
