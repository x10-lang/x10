/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * When the async place is not specified, it is take to be 'here'.
 *
 * @author Kemal Ebcioglu 4/2005
 */
public class AsyncTest5 extends x10Test {

	public def run(): boolean = {
		val A: Array[int](1) = Array.make[int](Dist.makeUnique());
		chk(Place.MAX_PLACES >= 2);
		finish async chk(A.dist(0) == here);
		// verify unique distribution
		for (val (i): Point in A)
                        for (val (j): Point in A)
                                chk(implies(A.dist(i) == A.dist(j), i == j));

		// verify async S is async(here)S
		finish ateach (val (i): Point in A) {
			async { atomic A(i) += i;
				chk(A.dist(i) == here);
				async(this) async chk(A.dist(0) == here);
			}
		}
		finish ateach (val (i): Point in A) {
			chk(A(i) == i);
		}
		return true;
	}

	static def implies(var x: boolean, var y: boolean): boolean = {
		return (!x) | y;
	}

	public static def main(var args: Rail[String]): void = {
		new AsyncTest5().execute();
	}
}
