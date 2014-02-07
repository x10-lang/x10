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
 * Minimal busy-waiting style test for async.
 * Written in this particular way to
 * a) always terminate
 * b) not use any other x10 construct
 */
public class AsyncTest extends x10Test {

	var flag: boolean = false;
	static N: long = 1000000000;

	public def run(): boolean = {
		var b: boolean = false;
		async  { atomic { this.flag = true; } }
		for (var i: long = 0; i < N*100; i++) {
			System.sleep(1);
			atomic { b = flag; }
			if (b) break;
		}
		return b;
	}

	public static def main(Rail[String])  {
		new AsyncTest().execute();
	}
}
