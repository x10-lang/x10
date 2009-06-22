/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks unboxing detects fails if object does not represent a boxed primitive.
 *          (i.e. from x10.compilergenerated.Boxed'Type')
 * Issue: obj is not a boxed primitive which throws a class cast exception.
 * @author vcave
 **/
 public class CastBoxedToPrimitive2 extends x10Test {

	public def run(): boolean = {
		try {
			var obj: x10.lang.Object = new CastBoxedToPrimitive2();
			var i: int = obj to int;
		} catch (var e: ClassCastException) {
			return true;
		}
		return false;
	}

	public static def main(var args: Rail[String]): void = {
		new CastBoxedToPrimitive2().execute();
	}
}
