/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import x10.util.Box;
import harness.x10Test;

/**
 * Testing a variable scope problem.
 * The second q's scope does not overlap with the first q.
 */
public class VariableScope extends x10Test {

	public def run(): boolean = {

		val N: int = 10;
		var e: Region = Region.makeRectangular(1, N); //(low, high)
		var r: Region = [e, e];
		var d: Dist = r->here;
		var n: int = 0;

		for (val p: Point in e)
                        for (val q: Point in e) {
				n++;
			}

		for (val p: Point in d) {
			var q: Box[Point] = null;
			n++;
		}

		return n == 2 * N * N;
	}

	public static def main(var args: Rail[String]): void = {
		new VariableScope().execute();
	}
}
