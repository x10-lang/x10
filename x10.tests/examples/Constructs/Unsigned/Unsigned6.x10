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
 * Simple unsigned test.
 */
public class Unsigned6 extends x10Test {

    public def run(): boolean = {
        val a: ubyte = 0;
        val b: ubyte = 1;
        // val c: ubyte = -1;
        val d: ubyte = 127;
        val e: ubyte = 128;
        val f: ubyte = 255;
        // val g: ubyte = 256;

        val h: ushort = 0;
        val i: ushort = 1;
        // val j: ushort = -1;
        val k: ushort = 127;
        val l: ushort = 128;
        val m: ushort = 255;
        val n: ushort = 256;
        val o: ushort = 32767;
        val p: ushort = 32768;
        val q: ushort = 65535;
        // val r: ushort = 65536;
        return true;
    }

    public static def main(Array[String](1)) = {
        new Unsigned6().execute();
    }
}
