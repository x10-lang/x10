/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Remote accesses in atomic must be checked by the compiler.
 *
 * Not yet caught by the compiler, error thrown at runtime.
 *
 * @author Kemal 4/2005, vj
 */
public class AtomicNonLocal_MustFailCompile extends x10Test {

	public def run(): boolean = {
		val A  = DistArray.make[int](Dist.makeUnique());
		chk(Place.MAX_PLACES >= 2);
		chk(A.dist(0) == here);
		chk(A.dist(1) != here);
		

		finish async  { A(0) += 1; }
		A(0) += 1;

		finish async  { A(1) += 1; }
		// this should ideally give a compile-time error.
		// wont until the compiler performs place checks on array accesses.
		atomic { A(1) += 1; } 
		return true;
	}

	public static def main(Rail[String]) {
		new AtomicNonLocal_MustFailCompile().execute();
	}

}
