/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Should report the type mismatch in the comparison in the loop, and fail
 * to compile gracefully.
 *
 * @author Bin Xin (xinb@cs.purdue.edu)
 */
public class NullableComparison extends x10Test {

	public const N: int = 6;

	public def run(): boolean = {
		val objList = Rail.make[Object!](N);
		val obj: Object = new Object();
		var i: int = N - 1;
		while (i > 0 && (obj != objList(i))) {
			i--;
		}
		if (i > 0)
			return false;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new NullableComparison().execute();
	}
}
