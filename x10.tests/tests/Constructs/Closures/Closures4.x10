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
public class Closures4 extends x10Test {
        static class C implements (long, long) => long {
                public operator this(i: long, j: long):long = {
                        return i+j;
                }
        }
                
	public def run(): boolean = {
                val x = new C();
                val j = x(3,4);
                return j == 7;
	}

	public static def main(var args: Rail[String]): void = {
		new Closures4().execute();
	}
}

