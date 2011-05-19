/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import harness.x10Test;

/**
 * Test casting of Int to UInt
 *
 * @author Salikh Zakirov 5/2011
 */
public class Cast1 extends x10Test {
    public def run(): boolean = {
	val u = 0xFFFFffffU;
	val i = u as Int;
	return (i == -1);
    }

    public static def main(Array[String]) {
        new Cast1().execute();
    }
}
