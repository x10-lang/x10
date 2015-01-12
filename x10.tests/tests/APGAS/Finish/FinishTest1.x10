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

/**
 * Minimal test for finish, using an async.
 * run() method returns true if successful, false otherwise.
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 */
public class FinishTest1 extends x10Test {

	var flag: boolean = false;

	public def run() {
		finish {
			async { atomic { flag = true; } }
		}
		var b: boolean = false;
		atomic { b = flag; }
		return b;
	}

	public static def main(args: Rail[String]) {
		new FinishTest1().execute();
	}
}
