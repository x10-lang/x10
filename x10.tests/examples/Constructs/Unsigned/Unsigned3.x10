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
public class Unsigned3 extends x10Test {

    public def run(): boolean = {
        val a = 0u < 1u;
        val b = 1u < 2u;
        val c = 0u < 0xffffffffu;
        val d = 0x7fffffffu < 0x80000000u;
        val e = 0u < 0x7fffffffu;
        val f = 0u < (-1 as uint);

        val g = 1u > 0u;
        val h = 2u > 1u;
        val i = 0xffffffffu > 0u;
        val j = 0x80000000u > 0x7fffffffu;
        val k = 0x7fffffffu > 0u;
        val l = (-1 as uint) > 0u;

        val m = 0u <= 1u;
        val n = 2u <= 2u;
        val o = 0u <= 0xffffffffu;
        val p = 0x7fffffffu <= 0x80000000u;
        val q = 0x7fffffffu <= 0x7fffffffu;
        val r = 0xffffffffu <= (-1 as uint);

        val s = 1u >= 0u;
        val t = 2u >= 2u;
        val u = 0xffffffffu >= 0u;
        val v = 0x80000000u >= 0x7fffffffu;
        val w = 0x7fffffffu >= 0x7fffffffu;
        val x = 0xffffffffu >= (-1 as uint);

        x10.io.Console.OUT.println("a " + a);
        x10.io.Console.OUT.println("b " + b);
        x10.io.Console.OUT.println("c " + c);
        x10.io.Console.OUT.println("d " + d);
        x10.io.Console.OUT.println("e " + e);
        x10.io.Console.OUT.println("f " + f);
        x10.io.Console.OUT.println("g " + g);
        x10.io.Console.OUT.println("h " + h);
        x10.io.Console.OUT.println("i " + i);
        x10.io.Console.OUT.println("j " + j);
        x10.io.Console.OUT.println("k " + k);
        x10.io.Console.OUT.println("l " + l);
        x10.io.Console.OUT.println("m " + m);
        x10.io.Console.OUT.println("n " + n);
        x10.io.Console.OUT.println("o " + o);
        x10.io.Console.OUT.println("p " + p);
        x10.io.Console.OUT.println("q " + q);
        x10.io.Console.OUT.println("r " + r);
        x10.io.Console.OUT.println("s " + s);
        x10.io.Console.OUT.println("t " + t);
        x10.io.Console.OUT.println("u " + u);
        x10.io.Console.OUT.println("v " + v);
        x10.io.Console.OUT.println("w " + w);
        x10.io.Console.OUT.println("x " + x);

        return a && b && c && d && e && f
            && g && h && i && j && k && l
            && m && n && o && p && q && r
            && s && t && u && v && w && x;
    }

    public static def main(Rail[String]) = {
        new Unsigned3().execute();
    }
}
