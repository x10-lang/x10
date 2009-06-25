/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Test for for loop on an array.
 *
 * @author vj
 */
public class ForLoopOnArray extends x10Test {

	public const N: int = 3;

	public def run(): boolean = {
		var a: Array[double] = new Array[double]([0..10], ((i): point): double => i to double);

		for (val (i): point in a) {
			if (a(i) != i) return false;
		}
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ForLoopOnArray().execute();
	}
}
