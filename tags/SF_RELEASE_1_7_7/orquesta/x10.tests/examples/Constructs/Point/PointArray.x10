/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Creating an array of points and assigning to its elements should work.
 *
 * @author igor, 1/2006
 */
public class PointArray extends x10Test {

	public def run(): boolean = {
		var p: Rail[point] = new Rail[point](1);
		p(0) = [1, 2];

		return (p(0)(0) == 1 && p(0)(1) == 2);
	}

	public static def main(var args: Rail[String]): void = {
		new PointArray().execute();
	}
}
