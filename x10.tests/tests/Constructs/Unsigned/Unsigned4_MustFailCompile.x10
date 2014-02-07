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
 * Simple unsigned test.
 */
public class Unsigned4_MustFailCompile extends x10Test {

    public def run(): boolean = {
        var a: int = 0n;
        var b: uint = 1un;
        val c = a < b; // ShouldNotBeERR (Cannot compare signed versus unsigned values.) ERR (No valid method call found for call in given type.)  
        return c;
    }

    public static def main(Rail[String]) = {
        new Unsigned4_MustFailCompile().execute();
    }
}
