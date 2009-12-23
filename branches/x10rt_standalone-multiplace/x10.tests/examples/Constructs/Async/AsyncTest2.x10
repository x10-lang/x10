/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing complex async bodies.
 *
 * @author Kemal Ebcioglu 4/2005
 */
public class AsyncTest2 extends x10Test {

	public def run(): boolean = {
		val NP: int = Place.MAX_PLACES;
		val A: Array[int]{rank==1} = Array.make[int](Dist.makeUnique());
		finish
			for (val (k): Point in 0..NP-1)
                                async (A.dist(k))
					ateach (val (i): Point{rank==A.rank} in A.dist)
                                                atomic A(i) += i;
		finish ateach (val (i): Point{rank==A.rank} in A.dist) { chk(A(i) == i*NP); }

		return true;
	}

	public static def main(Rail[String]) {
		new AsyncTest2().execute();
	}
}
