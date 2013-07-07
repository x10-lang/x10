/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */


import x10.interop.Java;


public class ByteArray {

    public static def main(args:Rail[String]):void {
        val z1 = true;
        val z2 = Java.toBoolean(Java.toBytes(z1));
        assert z1 == z2 : "z1 == z2";

        val c1 = '1';
        val c2 = Java.toChar(Java.toBytes(c1));
        assert c1 == c2 : "c1 == c2";

        val y1 = 1y;
        val y2 = Java.toByte(Java.toBytes(y1));
        assert y1 == y2 : "y1 == y2";

        val s1 = 1s;
        val s2 = Java.toShort(Java.toBytes(s1));
        assert s1 == s2 : "s1 == s2";

        val i1 = 1;
        val i2 = Java.toInt(Java.toBytes(i1));
        assert i1 == i2 : "i1 == i2";

        val l1 = 1l;
        val l2 = Java.toLong(Java.toBytes(l1));
        assert l1 == l2 : "l1 == l2";

        val f1 = 0.1f;
        val f2 = Java.toFloat(Java.toBytes(f1));
        assert f1 == f2 : "f1 == f2";

        val d1 = 0.1d;
        val d2 = Java.toDouble(Java.toBytes(d1));
        assert d1 == d2 : "d1 == d2";

        val S1 = "1";
        val S2 = Java.toString(Java.toBytes(S1));
        assert S1.equals(S2) : "S1.equals(S2)";
    }

}
