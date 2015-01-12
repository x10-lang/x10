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
 * Check lazy, per-field, per-place initialization semantics of static fields.
 * Valid for X10 2.2.3 and later.
 * 
 * @author mtake 7/2012
 */
public class InitStaticField3 extends x10Test {

    static val a = 2 + 3;
    static val b = 2 - 3;
    static val c = 2 * 3;
    static val d = 2 / 3;
    static val e = 2 % 3;
    static val f = 2 << 3n;
    static val g = 2 >> 3n;
    static val h = 2 >>> 3n;
    static val i = 2 & 3;
    static val j = 2 | 3;
    static val k = 2 ^ 3;
    static val l = ~2;

    static val m = 2 > 3;
    static val n = 2 >= 3;
    static val o = 2 < 3;
    static val p = 2 <= 3;
    static val q = 2 == 3;
    static val r = 2 != 3;

    static val za = false & true;
    static val zb = true | false;
    static val zc = true ^ false;
    static val zd = !true;
    static val ze = false && true;
    static val zf = true || false;

    public def run():Boolean {
        chk(a == 5);
        chk(b == -1);
        chk(c == 6);
        chk(d == 0);
        chk(e == 2);
        chk(f == 16);
        chk(g == 0);
        chk(h == 0);
        chk(i == 2);
        chk(j == 3);
        chk(k == 1);
        chk(l == 0xfffffffffffffffd);

        chk(m == false);
        chk(n == false);
        chk(o == true);
        chk(p == true);
        chk(q == false);
        chk(r == true);

        chk(za == false);
        chk(zb == true);
        chk(zc == true);
        chk(zd == false);
        chk(ze == false);
        chk(zf == true);

        return true;
    }

    public static def main(Rail[String]) {
        new InitStaticField3().execute();
    }

}
