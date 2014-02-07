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

import harness.x10Test;

/**
 * Minimal test for atomic.  If the atomic code is not executed atomically
 * the other activity can check that the value difference is not N.
 */
public class AtomicTest extends x10Test {

	var val_: long = 0;
	static N: long = 1000;
	var startCount: long = 0;
	var endCount: long = N;

    public def run(): boolean = {
	var b: boolean; // temp
	async  {
	    atomic {
		startCount = val_;
		for (var i: int = 0n; i < N; i++) val_++;
		endCount = val_;
	    }
	}
	for (var i: long = 0; i < N*100; i++) {
	    atomic { val_ = i; b = (endCount != 0L); }
	    if (b) break;
	}
	// need a memory fence here
	atomic { b = (startCount + N == endCount); }
	return b;
    }

	public static def main(Rail[String]) {
	    new AtomicTest().execute();
	}
}
