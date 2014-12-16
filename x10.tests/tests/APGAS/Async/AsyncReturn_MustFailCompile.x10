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
 * Testing returns in an async body. 
 * New semantics for X10 1.7 Cannot return from the body of an async.
 *
 * @author vj
 * updated 03/15/09
 */
public class AsyncReturn_MustFailCompile extends x10Test {

	public def run(): boolean = {
		finish async {
				return; // ERR: Cannot return from an async.
		}
		return true;
	}

	public static def main(Rail[String]) {
		new AsyncReturn_MustFailCompile().execute();
	}
}
