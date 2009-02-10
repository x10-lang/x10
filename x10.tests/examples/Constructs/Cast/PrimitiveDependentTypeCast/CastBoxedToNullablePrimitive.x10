/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks cast leading to primitive unboxing works.
 * @author vcave
 **/
 public class CastBoxedToNullablePrimitive extends x10Test {

	public def run(): boolean = {
		// transformed to (nullable<int>) ((BoxedInteger) obj).intValue();
		var i: Box[int] = mth() as Box[int];
		return true;
	}
	
	public def mth(): x10.lang.Object = {
		// boxed as Box[int];
		return 3;
	}
	public static def main(var args: Rail[String]): void = {
		new CastBoxedToNullablePrimitive().execute();
	}
}
