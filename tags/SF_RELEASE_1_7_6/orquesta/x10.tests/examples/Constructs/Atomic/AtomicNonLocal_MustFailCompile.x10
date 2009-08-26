/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Remote accesses in atomic must be checked by the compiler.
 *
 * occur, but the error is still caught at run time.
 *
 * @author Kemal 4/2005, vj
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.

public class AtomicNonLocal_MustFailCompile extends x10Test {

	public def run(): boolean = {
		val A: Array[int] = new Array[int](dist.makeUnique());
		chk(place.MAX_PLACES >= 2);
		chk(A.dist(0) == here);
		chk(A.dist(1) != here);
		

		finish async(here) { A(0) += 1; }
		A(0) += 1;

		finish async(here) { A(1) += 1; }
		atomic { A(1) += 1; } 
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new AtomicNonLocal_MustFailCompile().execute();
	}

}
