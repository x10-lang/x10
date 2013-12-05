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


public class JavaInteropSerialization {

    public static def main(args:Rail[String]):void {
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

        val ao1 = Java.newArray[Any](0n);
        val ao2 = Java.deserialize(Java.serialize(ao1)) as Java.array[Any];
        assert ao1.length == ao2.length : "ao1.length == ao2.length";
        for (var i:Int = 0n; i < ao1.length; ++i) {
            val o1 = ao1(i);
            val o2 = ao2(i);
            assert o1 == null ? o2 == null : o1.equals(o2);
        }

        val aI1 = Java.newArray[Int](0n);
        val aI2 = Java.deserialize(Java.serialize(aI1)) as Java.array[Int];
        assert aI1.length == aI2.length : "aI1.length == aI2.length";
        for (var i:Int = 0n; i < aI1.length; ++i) {
            val I1 = aI1(i);
            val I2 = aI2(i);
            assert I1.equals(I2) : "I1.equals(I2)";
        }

        val Ao1_:Any = Java.newArray[Any](0n);
        val Ao2_ = Java.deserialize(Java.serialize(Ao1_));
        val Ao1 = Ao1_ as Java.array[Any];
        val Ao2 = Ao2_ as Java.array[Any];
        assert Ao1.length == Ao2.length : "Ao1.length == Ao2.length";
        for (var i:Int = 0n; i < Ao1.length; ++i) {
            val o1 = Ao1(i);
            val o2 = Ao2(i);
            assert (o1 == null ? o2 == null : o1.equals(o2)) : "o1 == null ? o2 == null : o1.equals(o2)";
        }

        val AI1_:Any = Java.newArray[Int](0n);
        val AI2_ = Java.deserialize(Java.serialize(AI1_));
        val AI1 = AI1_ as Java.array[Int];
        val AI2 = AI2_ as Java.array[Int];
        assert AI1.length == AI2.length : "AI1.length == AI2.length";
        for (var i:Int = 0n; i < AI1.length; ++i) {
            val I1 = AI1(i);
            val I2 = AI2(i);
            assert I1.equals(I2) : "I1.equals(I2)";
        }

        Console.OUT.println("Passed");
    }

}
