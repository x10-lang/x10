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

import harness.x10Test;

/**
 * Test rail assignments.
 *
 * @author nystrom 8/2008
 */
public class Assign1 extends x10Test {
	public def run(): boolean = {
                val r = new Rail[int](1, (int) => 0);
                r(0) = 0;
                r(r(0)) += 5;
                return r(0) == 5;
	}

	public static def main(var args: Rail[String]): void = {
		new Assign1().execute();
	}
}

