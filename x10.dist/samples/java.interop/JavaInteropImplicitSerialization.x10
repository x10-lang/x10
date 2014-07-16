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


import x10.interop.Java;


public class JavaInteropImplicitSerialization {

    private static def simpleTest():void {
        val y1 = 1y;
        val y2 = at (Place.places().next(here)) { y1 };
        assert y1 == y2 : "y1 == y2";

        val s1 = 1s;
        val s2 = at (Place.places().next(here)) { s1 };
        assert s1 == s2 : "s1 == s2";

        val i1 = 1n;
        val i2 = at (Place.places().next(here)) { i1 };
        assert i1 == i2 : "i1 == i2";

        val l1 = 1l;
        val l2 = at (Place.places().next(here)) { l1 };
        assert l1 == l2 : "l1 == l2";

        val uy1 = 1uy;
        val uy2 = at (Place.places().next(here)) { uy1 };
        assert uy1 == uy2 : "uy1 == uy2";

        val us1 = 1us;
        val us2 = at (Place.places().next(here)) { us1 };
        assert us1 == us2 : "us1 == us2";

        val ui1 = 1un;
        val ui2 = at (Place.places().next(here)) { ui1 };
        assert ui1 == ui2 : "ui1 == ui2";

        val ul1 = 1ul;
        val ul2 = at (Place.places().next(here)) { ul1 };
        assert ul1 == ul2 : "ul1 == ul2";

        val f1 = 0.1f;
        val f2 = at (Place.places().next(here)) { f1 };
        assert f1 == f2 : "f1 == f2";

        val d1 = 0.1d;
        val d2 = at (Place.places().next(here)) { d1 };
        assert d1 == d2 : "d1 == d2";

        val c1 = '1';
        val c2 = at (Place.places().next(here)) { c1 };
        assert c1 == c2 : "c1 == c2";

        val z1 = true;
        val z2 = at (Place.places().next(here)) { z1 };
        assert z1 == z2 : "z1 == z2";

        val S1 = "1";
        val S2 =  at (Place.places().next(here)) { S1 };
        assert S1.equals(S2) : "S1.equals(S2)";
    }

    private static def typedArrayTest():void {
        val aA1 = Java.newArray[Any](10n);
        for (var i:Int = 0n; i < aA1.length; ++i) { aA1(i) = i.toString(); }
        val aA2 = at (Place.places().next(here)) { aA1 };
        assert aA1.length == aA2.length : "aA1.length == aA2.length";
        for (var i:Int = 0n; i < aA1.length; ++i) {
            val A1 = aA1(i);
            val A2 = aA2(i);
            assert (A1 == null ? A2 == null : A1.equals(A2)) : "A1 == null ? A2 == null : A1.equals(A2)";
        }

        val aS1 = Java.newArray[String](10n);
        for (var i:Int = 0n; i < aS1.length; ++i) { aS1(i) = i.toString(); }
        val aS2 = at (Place.places().next(here)) { aS1 };
        assert aS1.length == aS2.length : "aS1.length == aS2.length";
        for (var i:Int = 0n; i < aS1.length; ++i) {
            val S1 = aS1(i);
            val S2 = aS2(i);
            assert (S1 == null ? S2 == null : S1.equals(S2)) : "S1 == null ? S2 == null : S1.equals(S2)";
        }

        val ai1 = Java.newArray[Int](10n);
        for (var i:Int = 0n; i < ai1.length; ++i) { ai1(i) = i; }
        val ai2 = at (Place.places().next(here)) { ai1 };
        assert ai1.length == ai2.length : "ai1.length == ai2.length";
        for (var i:Int = 0n; i < ai1.length; ++i) {
            val i1 = ai1(i);
            val i2 = ai2(i);
            assert i1.equals(i2) : "i1.equals(i2)";
        }

        val aCS1 = Java.newArray[Comparable[String]](10n);
        for (var i:Int = 0n; i < aCS1.length; ++i) { aCS1(i) = i.toString(); }
        val aCS2 = at (Place.places().next(here)) { aCS1 };
        assert aCS1.length == aCS2.length : "aCS1.length == aCS2.length";
        for (var i:Int = 0n; i < aCS1.length; ++i) {
            val CS1 = aCS1(i);
            val CS2 = aCS2(i);
            assert (CS1 == null ? CS2 == null : CS1.equals(CS2)) : "CS1 == null ? CS2 == null : CS1.equals(CS2)";
        }

        val aCi1 = Java.newArray[Comparable[Int]](10n);
        for (var i:Int = 0n; i < aCi1.length; ++i) { aCi1(i) = i; }
        val aCi2 = at (Place.places().next(here)) { aCi1 };
        assert aCi1.length == aCi2.length : "aCi1.length == aCi2.length";
        for (var i:Int = 0n; i < aCi1.length; ++i) {
            val Ci1 = aCi1(i);
            val Ci2 = aCi2(i);
            assert Ci1.equals(Ci2) : "Ci1.equals(Ci2)";
        }
    }

    private static def untypedArrayTest():void {
        val aA1 = Java.newArray[Any](10n);
        for (var i:Int = 0n; i < aA1.length; ++i) { aA1(i) = i.toString(); }
        val aA1_:Any = aA1;
        val aA2_ = at (Place.places().next(here)) { aA1_ };
        val aA2 = aA2_ as Java.array[Any];
        assert aA1.length == aA2.length : "aA1.length == aA2.length";
        for (var i:Int = 0n; i < aA1.length; ++i) {
            val A1 = aA1(i);
            val A2 = aA2(i);
            assert (A1 == null ? A2 == null : A1.equals(A2)) : "A1 == null ? A2 == null : A1.equals(A2)";
        }

        val aS1 = Java.newArray[String](10n);
        for (var i:Int = 0n; i < aS1.length; ++i) { aS1(i) = i.toString(); }
        val aS1_:Any = aS1;
        val aS2_ = at (Place.places().next(here)) { aS1_ };
        val aS2 = aS2_ as Java.array[String];
        assert aS1.length == aS2.length : "aS1.length == aS2.length";
        for (var i:Int = 0n; i < aS1.length; ++i) {
            val S1 = aS1(i);
            val S2 = aS2(i);
            assert (S1 == null ? S2 == null : S1.equals(S2)) : "S1 == null ? S2 == null : S1.equals(S2)";
        }

        val ai1 = Java.newArray[Int](10n);
        for (var i:Int = 0n; i < ai1.length; ++i) { ai1(i) = i; }
        val ai1_:Any = ai1;
        val ai2_ = at (Place.places().next(here)) { ai1_ };
        val ai2 = ai2_ as Java.array[Int];
        assert ai1.length == ai2.length : "ai1.length == ai2.length";
        for (var i:Int = 0n; i < ai1.length; ++i) {
            val i1 = ai1(i);
            val i2 = ai2(i);
            assert i1.equals(i2) : "i1.equals(i2)";
        }

        val aCS1 = Java.newArray[Comparable[String]](10n);
        for (var i:Int = 0n; i < aCS1.length; ++i) { aCS1(i) = i.toString(); }
        val aCS1_:Any = aCS1;
        val aCS2_ = at (Place.places().next(here)) { aCS1_ };
        val aCS2 = aCS2_ as Java.array[Comparable[String]];
        assert aCS1.length == aCS2.length : "aCS1.length == aCS2.length";
        for (var i:Int = 0n; i < aCS1.length; ++i) {
            val CS1 = aCS1(i);
            val CS2 = aCS2(i);
            assert (CS1 == null ? CS2 == null : CS1.equals(CS2)) : "CS1 == null ? CS2 == null : CS1.equals(CS2)";
        }

        val aCi1 = Java.newArray[Comparable[Int]](10n);
        for (var i:Int = 0n; i < aCi1.length; ++i) { aCi1(i) = i; }
        val aCi1_:Any = aCi1;
        val aCi2_ = at (Place.places().next(here)) { aCi1_ };
        val aCi2 = aCi2_ as Java.array[Comparable[Int]];
        assert aCi1.length == aCi2.length : "aCi1.length == aCi2.length";
        for (var i:Int = 0n; i < aCi1.length; ++i) {
            val Ci1 = aCi1(i);
            val Ci2 = aCi2(i);
            assert Ci1.equals(Ci2) : "Ci1.equals(Ci2)";
        }
    }

    public static def main(args:Rail[String]):void {
        simpleTest();
        typedArrayTest();
        untypedArrayTest();
        Console.OUT.println("All Passed!");
    }

}
