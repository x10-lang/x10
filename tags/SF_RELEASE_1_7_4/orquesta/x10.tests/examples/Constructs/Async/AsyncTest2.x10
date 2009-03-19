/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Testing complex async bodies.
 *
 * @author Kemal Ebcioglu 4/2005
 */
public class AsyncTest2 extends x10Test {

	public def run(): boolean = {
		val NP: int = place.MAX_PLACES;
		val A: Array[int] = new Array[int](dist.makeUnique());
		finish
			for (val (k): point in [0..NP-1])
                                async (A.dist(k))
					ateach (val (i): point in A)
                                                atomic A(i) += i;
		finish ateach (val (i): point in A) { chk(A(i) == i*NP); }

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new AsyncTest2().execute();
	}
}
