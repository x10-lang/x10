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
 * Minimal test for atomic method qualifier.
 */
public class AtomicMethodTest extends x10Test {

    var val_: long = 0;
    public static N: long = 1000;
    var startCount: long = 0;
    var endCount: long = N;
    atomic def body() {
	   startCount = this.val_;
	   for (i in 0..(N-1)) this.val_++;
	   endCount = this.val_;
    }

    public def run()  {
	   async body();
	   for (var i: long = 0; i < N*N; i++) {
	       var b: boolean; // temp
	       atomic { this.val_ = i; b = (endCount != 0L); }
	       if (b) break;
	   }
	   // assuming atomics follow program order
	   var b: boolean; // temp;
	   atomic { b = (startCount + N == endCount); }
	   return b;
    }

	public static def main(Rail[String]) {
	    new AtomicMethodTest().execute();
	}
}
