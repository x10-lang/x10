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


public class JavaInteropSerialization2 {

    public static def main(args:Rail[String]):void {
        val y1 = 1y;
        val y2 = at (here.next()) { y1 };
        assert y1 == y2 : "y1 == y2";

        val s1 = 1s;
        val s2 = at (here.next()) { s1 };
        assert s1 == s2 : "s1 == s2";

        val i1 = 1n;
        val i2 = at (here.next()) { i1 };
        assert i1 == i2 : "i1 == i2";

        val l1 = 1l;
        val l2 = at (here.next()) { l1 };
        assert l1 == l2 : "l1 == l2";

        val uy1 = 1uy;
        val uy2 = at (here.next()) { uy1 };
        assert uy1 == uy2 : "uy1 == uy2";

        val us1 = 1us;
        val us2 = at (here.next()) { us1 };
        assert us1 == us2 : "us1 == us2";

        val ui1 = 1un;
        val ui2 = at (here.next()) { ui1 };
        assert ui1 == ui2 : "ui1 == ui2";

        val ul1 = 1ul;
        val ul2 = at (here.next()) { ul1 };
        assert ul1 == ul2 : "ul1 == ul2";

        val f1 = 0.1f;
        val f2 = at (here.next()) { f1 };
        assert f1 == f2 : "f1 == f2";

        val d1 = 0.1d;
        val d2 = at (here.next()) { d1 };
        assert d1 == d2 : "d1 == d2";

        val c1 = '1';
        val c2 = at (here.next()) { c1 };
        assert c1 == c2 : "c1 == c2";

        val z1 = true;
        val z2 = at (here.next()) { z1 };
        assert z1 == z2 : "z1 == z2";

        val S1 = "1";
        val S2 =  at (here.next()) { S1 };
        assert S1.equals(S2) : "S1.equals(S2)";

        val ao1 = Java.newArray[Any](0n);
        val ao2 = at (here.next()) { ao1 };
        assert ao1.length == ao2.length : "ao1.length == ao2.length";
        for (var i:Int = 0n; i < ao1.length; ++i) {
            val o1 = ao1(i);
            val o2 = ao2(i);
            assert (o1 == null ? o2 == null : o1.equals(o2)) : "o1 == null ? o2 == null : o1.equals(o2)";
        }

        val aI1 = Java.newArray[Int](0n);
        val aI2 = at (here.next()) { aI1 };
        assert aI1.length == aI2.length : "aI1.length == aI2.length";
        for (var i:Int = 0n; i < aI1.length; ++i) {
            val I1 = aI1(i);
            val I2 = aI2(i);
            assert I1.equals(I2) : "I1.equals(I2)";
        }

        Console.OUT.println("Passed");
    }

}
