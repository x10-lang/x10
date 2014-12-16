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


public class JavaInteropExplicitSerialization {

    private static def simpleTest():void {
        val y1 = 1y;
        val y2 = Java.deserialize(Java.serialize(y1)) as Byte;
        assert y1 == y2 : "y1 == y2";

        val s1 = 1s;
        val s2 = Java.deserialize(Java.serialize(s1)) as Short;
        assert s1 == s2 : "s1 == s2";

        val i1 = 1n;
        val i2 = Java.deserialize(Java.serialize(i1)) as Int;
        assert i1 == i2 : "i1 == i2";

        val l1 = 1l;
        val l2 = Java.deserialize(Java.serialize(l1)) as Long;
        assert l1 == l2 : "l1 == l2";

        val uy1 = 1uy;
        val uy2 = Java.deserialize(Java.serialize(uy1)) as UByte;
        assert uy1 == uy2 : "uy1 == uy2";

        val us1 = 1us;
        val us2 = Java.deserialize(Java.serialize(us1)) as UShort;
        assert us1 == us2 : "us1 == us2";

        val ui1 = 1un;
        val ui2 = Java.deserialize(Java.serialize(ui1)) as UInt;
        assert ui1 == ui2 : "ui1 == ui2";

        val ul1 = 1ul;
        val ul2 = Java.deserialize(Java.serialize(ul1)) as ULong;
        assert ul1 == ul2 : "ul1 == ul2";

        val f1 = 0.1f;
        val f2 = Java.deserialize(Java.serialize(f1)) as Float;
        assert f1 == f2 : "f1 == f2";

        val d1 = 0.1d;
        val d2 = Java.deserialize(Java.serialize(d1)) as Double;
        assert d1 == d2 : "d1 == d2";

        val c1 = '1';
        val c2 = Java.deserialize(Java.serialize(c1)) as Char;
        assert c1 == c2 : "c1 == c2";

        val z1 = true;
        val z2 = Java.deserialize(Java.serialize(z1)) as Boolean;
        assert z1 == z2 : "z1 == z2";

        val S1 = "1";
        val S2 = Java.deserialize(Java.serialize(S1)) as String;
        assert S1.equals(S2) : "S1.equals(S2)";
    }

    private static def typedArrayTest():void {
        val aA1 = Java.newArray[Any](10n);
        for (var i:Int = 0n; i < aA1.length; ++i) { aA1(i) = i.toString(); }
        val aA2 = Java.deserialize(Java.serialize(aA1)) as Java.array[Any];
        assert aA1.length == aA2.length : "aA1.length == aA2.length";
        for (var i:Int = 0n; i < aA1.length; ++i) {
            val A1 = aA1(i);
            val A2 = aA2(i);
            assert (A1 == null ? A2 == null : A1.equals(A2)) : "A1 == null ? A2 == null : A1.equals(A2)";
        }

        val aS1 = Java.newArray[String](10n);
        for (var i:Int = 0n; i < aS1.length; ++i) { aS1(i) = i.toString(); }
        val aS2 = Java.deserialize(Java.serialize(aS1)) as Java.array[String];
        assert aS1.length == aS2.length : "aS1.length == aS2.length";
        for (var i:Int = 0n; i < aS1.length; ++i) {
            val S1 = aS1(i);
            val S2 = aS2(i);
            assert (S1 == null ? S2 == null : S1.equals(S2)) : "S1 == null ? S2 == null : S1.equals(S2)";
        }

        val ai1 = Java.newArray[Int](10n);
        for (var i:Int = 0n; i < ai1.length; ++i) { ai1(i) = i; }
        val ai2 = Java.deserialize(Java.serialize(ai1)) as Java.array[Int];
        assert ai1.length == ai2.length : "ai1.length == ai2.length";
        for (var i:Int = 0n; i < ai1.length; ++i) {
            val i1 = ai1(i);
            val i2 = ai2(i);
            assert i1.equals(i2) : "i1.equals(i2)";
        }

        val aCS1 = Java.newArray[Comparable[String]](10n);
        for (var i:Int = 0n; i < aCS1.length; ++i) { aCS1(i) = i.toString(); }
        val aCS2 = Java.deserialize(Java.serialize(aCS1)) as Java.array[Comparable[String]];
        assert aCS1.length == aCS2.length : "aCS1.length == aCS2.length";
        for (var i:Int = 0n; i < aCS1.length; ++i) {
            val CS1 = aCS1(i);
            val CS2 = aCS2(i);
            assert (CS1 == null ? CS2 == null : CS1.equals(CS2)) : "CS1 == null ? CS2 == null : CS1.equals(CS2)";
        }

        val aCi1 = Java.newArray[Comparable[Int]](10n);
        for (var i:Int = 0n; i < aCi1.length; ++i) { aCi1(i) = i; }
        val aCi2 = Java.deserialize(Java.serialize(aCi1)) as Java.array[Comparable[Int]];
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
        val aA2_ = Java.deserialize(Java.serialize(aA1_));
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
        val aS2_ = Java.deserialize(Java.serialize(aS1_));
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
        val ai2_ = Java.deserialize(Java.serialize(ai1_));
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
        val aCS2_ = Java.deserialize(Java.serialize(aCS1_));
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
        val aCi2_ = Java.deserialize(Java.serialize(aCi1_));
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
