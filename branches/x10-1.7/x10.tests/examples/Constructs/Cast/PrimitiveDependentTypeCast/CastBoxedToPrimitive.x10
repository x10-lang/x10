/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks boxing/unboxing works properly.
 * @author vcave
 **/
 public class CastBoxedToPrimitive extends x10Test {

	public def run(): boolean = {
		// boxed to new BoxedInteger(3)
		var obj: x10.lang.Object = 3;
		// unboxed as : ((BoxedInteger) obj).intValue()
		var i: int = obj as int;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastBoxedToPrimitive().execute();
	}
}
