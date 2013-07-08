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
        val y2 = Java.deserializeByte(Java.serializeByte(y1));
        assert y1 == y2 : "y1 == y2";

        val s1 = 1s;
        val s2 = Java.deserializeShort(Java.serializeShort(s1));
        assert s1 == s2 : "s1 == s2";

        val i1 = 1;
        val i2 = Java.deserializeInt(Java.serializeInt(i1));
        assert i1 == i2 : "i1 == i2";

        val l1 = 1l;
        val l2 = Java.deserializeLong(Java.serializeLong(l1));
        assert l1 == l2 : "l1 == l2";

        val uy1 = 1uy;
        val uy2 = Java.deserializeUByte(Java.serializeUByte(uy1));
        assert uy1 == uy2 : "uy1 == uy2";

        val us1 = 1us;
        val us2 = Java.deserializeUShort(Java.serializeUShort(us1));
        assert us1 == us2 : "us1 == us2";

        val ui1 = 1u;
        val ui2 = Java.deserializeUInt(Java.serializeUInt(ui1));
        assert ui1 == ui2 : "ui1 == ui2";

        val ul1 = 1ul;
        val ul2 = Java.deserializeULong(Java.serializeULong(ul1));
        assert ul1 == ul2 : "ul1 == ul2";

        val f1 = 0.1f;
        val f2 = Java.deserializeFloat(Java.serializeFloat(f1));
        assert f1 == f2 : "f1 == f2";

        val d1 = 0.1d;
        val d2 = Java.deserializeDouble(Java.serializeDouble(d1));
        assert d1 == d2 : "d1 == d2";

        val c1 = '1';
        val c2 = Java.deserializeChar(Java.serializeChar(c1));
        assert c1 == c2 : "c1 == c2";

        val z1 = true;
        val z2 = Java.deserializeBoolean(Java.serializeBoolean(z1));
        assert z1 == z2 : "z1 == z2";

        val S1 = "1";
        val S2 = Java.deserialize(Java.serialize(S1)) as String;
        assert S1.equals(S2) : "S1.equals(S2)";

    }

}
