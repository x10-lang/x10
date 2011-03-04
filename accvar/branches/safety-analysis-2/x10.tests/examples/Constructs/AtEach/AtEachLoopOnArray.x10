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

import harness.x10Test;

/**
 * Test for an ateach loop on an array.
 *
 * @author vj
 */
public class AtEachLoopOnArray extends x10Test {
    var success: boolean = true;

    public def run(): boolean = {
	val A: DistArray[double](1) =
	DistArray.make[double]([0..10]->here, ((i): Point): double => i as double);
	
	finish ateach (val (i): Point(1) in A)
	if (A(i) != i)
	    async (this) atomic { success = false; }
	return success;
    }

	public static def main(var args: Rail[String]): void = {
	    new AtEachLoopOnArray().execute();
	}
}
