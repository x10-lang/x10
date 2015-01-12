/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;
import x10.compiler.Unroll;

/**
 *  for loop unrolling
 *
 * @author dcunnin, 05/20/2013
 */
public class ForLoopUnroll extends x10Test {

	public def run(): boolean = {

        var x:Long = 0l;

        @Unroll(10) for (i in 5 .. 98) {
            x += i;
        }

        @Unroll(10) for (i in 5l .. 98l) {
            x += i;
        }

        for (i in 5..98) {
            x -= i;
        }

        for (i in 5l..98) {
            x -= i;
        }

		return x == 0l;
	}

	public static def main(Rail[String]) {
		new ForLoopUnroll().execute();
	}
}
