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
 * Test rail assignments.
 *
 * @author nystrom 8/2008
 */
public class Assign1 extends x10Test {
	public def run(): boolean = {
                val r = new Rail[int](1, 0n);
                r(0) = 0n;
                r(r(0)) += 5n;
                return r(0) == 5n;
	}

	public static def main(var args: Rail[String]): void = {
		new Assign1().execute();
	}
}

