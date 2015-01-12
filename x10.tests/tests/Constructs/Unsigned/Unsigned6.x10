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
public class Unsigned6 extends x10Test {

    public def run(): boolean = {
        val a: ubyte = 0uy;
        val b: ubyte = 1uy;
        // val c: ubyte = -1;
        val d: ubyte = 127uy;
        val e: ubyte = 128uy;
        val f: ubyte = 255uy;
        // val g: ubyte = 256;

        val h: ushort = 0us;
        val i: ushort = 1us;
        // val j: ushort = -1;
        val k: ushort = 127us;
        val l: ushort = 128us;
        val m: ushort = 255us;
        val n: ushort = 256us;
        val o: ushort = 32767us;
        val p: ushort = 32768us;
        val q: ushort = 65535us;
        // val r: ushort = 65536;
        return true;
    }

    public static def main(Rail[String]) = {
        new Unsigned6().execute();
    }
}
