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

 

/**
 * Minimal test for async.
 * Uses busy-wait to check for execution of async.
 * run() method returns true if successful, false otherwise.
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 */
public class AsyncTest1   {

	var flag: boolean;
	public def run(): boolean = {
		async at (this) { atomic { flag = true; } }
		var b: boolean;
		do {
			atomic { b = flag; }
		} while (!b);
		return b;
	}

	public static def main(args: Rail[String]) {
		new AsyncTest1().run ();
	}
}
