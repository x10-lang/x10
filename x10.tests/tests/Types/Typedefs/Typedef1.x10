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
 * Test type defs in method bodies.
 *
 * @author nystrom 8/2008
 */
public class Typedef1 extends x10Test {
                static type foo = int;
                static type bar = Typedef1;

	public def run(): boolean = {
                val x: foo = 3n;
                val y: bar = this;
		return x == 3n && y == this;
	}

	public static def main(var args: Rail[String]): void = {
		new Typedef1().execute();
	}
}

