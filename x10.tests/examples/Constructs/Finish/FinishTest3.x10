/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

import harness.x10Test;

/**
 * Test for nested finish blocks.
 */
public class FinishTest3 extends x10Test {

	var flag: boolean = false;

	public def run() {
		var a : Int = 1;
		finish async {
			try {
			    val a1 = a;
			} finally {}
			var b : Int = 2;
			finish async {
				val a2 = a;
				val b2 = b;
				flag = (a2+b2) == 3;
			}
		}
		return flag;
	}

	public static def main(args: Array[String](1)) {
		new FinishTest3().execute();
	}
}
