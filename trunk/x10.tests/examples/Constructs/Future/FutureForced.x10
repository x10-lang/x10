/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/** Check that forced works correctly.
 * Future test.
 */
public class FutureForced extends x10Test {
	public def run(): boolean = {
		val x = future  41;
		val v = x();
		return x.forced();
	}

	public static def main(var args: Rail[String]): void = {
		new FutureForced().execute();
	}
}
