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
 * Simple unsigned test.
 */
public class Unsigned2 extends x10Test {

    public def run(): boolean = {
        val x: uint = 1un;
        x10.io.Console.OUT.println(x.toString());
        x10.io.Console.OUT.println(x as int);
        val y: uint = 0xffffffffun;
        x10.io.Console.OUT.println(y.toString());
        x10.io.Console.OUT.println(y as int);
        val w: uint = 0x80000000un;
        x10.io.Console.OUT.println(y.toString());
        x10.io.Console.OUT.println(y as int);
        val z: uint = 1un;
        x10.io.Console.OUT.println(z.toString());
        x10.io.Console.OUT.println(z as int);
        return (x as int) == 1n
            && (y as int) == 0xffffffffn
            && (w as int) == 0x80000000n
            && (z as int) == 1n;
    }

    public static def main(Rail[String]) = {
        new Unsigned2().execute();
    }
}
