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
public class Unsigned1 extends x10Test {

    public def run(): boolean {
        val x: uint = 1n as uint;
        x10.io.Console.OUT.println(x.toString());
        x10.io.Console.OUT.println(x as int);
        val y: uint = (Int.MAX_VALUE as uint) + (1n as uint);
        x10.io.Console.OUT.println(y.toString());
        x10.io.Console.OUT.println(y as int);
        return (x as int) == 1n && (y as int) == Int.MIN_VALUE;
    }

    public static def main(Rail[String]) {
        new Unsigned1().execute();
    }
}
