/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

/**
 * Test Fibonacci GLB version
 */
import harness.x10Test;

// SOURCEPATH: x10.dist/samples/GLB/fib
public class FibGTest extends x10Test {
    public def run():boolean {
	val args = new Rail[String](1L);
	args(0)="30";
	val res = FibG.mainTest(args);
	chk(res(0) == 832040L);
	return true;
    }

    public static def main(args:Rail[String]) {
	new FibGTest().execute();
    }
}
