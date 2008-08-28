/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Testing a variable scope problem.
 * The second q's scope does not overlap with the first q.
 */
public class VariableScope extends x10Test {

	public def run(): boolean = {

		final val N: int = 10;
		var e: region = region.factory.region(1, N); //(low, high)
		var r: region = region.factory.region(new Array[region] { e, e });
		var d: dist = r->here;
		var n: int = 0;

		for (val p: point in e) for (val q: point in e) {
				n++;
			}

		for (val p: point in d) {
			var q: point = null;
			n++;
		}

		return n == 2 * N * N;
	}

	public static def main(var args: Rail[String]): void = {
		new VariableScope().execute();
	}
}
