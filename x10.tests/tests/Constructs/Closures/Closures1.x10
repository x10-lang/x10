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
 * Test closures.
 *
 * @author nystrom 8/2008
 */
public class Closures1 extends x10Test {
	public def run(): boolean = {
                val j = ((i:long) => 3+i)(4);
                return j == 7;
	}

	public static def main(var args: Rail[String]): void = {
		new Closures1().execute();
	}
}

