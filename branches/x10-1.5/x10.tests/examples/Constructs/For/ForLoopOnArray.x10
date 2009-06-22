/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test for for loop on an array.
 *
 * @author vj
 */
public class ForLoopOnArray extends x10Test {

	public const N: int = 3;

	public def run(): boolean = {
		var a: Array[double](1) = Array.make[double]([0..10], ((i): Point): double => i as double);

		for (val (i): Point in a.region) {
			if (a(i) != i) return false;
		}
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ForLoopOnArray().execute();
	}
}
