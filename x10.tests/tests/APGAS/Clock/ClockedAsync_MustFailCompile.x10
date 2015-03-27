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
 * A clocked async must be invoked within a statically enclosing clocked finish.
 */
public class ClockedAsync_MustFailCompile extends x10Test {


	public def run(): boolean {
	 clocked async ; // ERR: clocked async must be invoked inside a statically enclosing clocked finish.
		return false;
	}

	public static def main(Rail[String]) {
		new ClockedAsync_MustFailCompile().execute();
	}
}
