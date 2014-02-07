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
 * Testing factorial
 */
public class FactTest extends x10Test {

    public def run() = fact(5) == 120;
    public def fact(var v: Long):Long = v <=1 ? 1 : v*fact(v-1);
    public static def main(var args: Rail[String]): void = {
	new FactTest().execute();
    }
}
