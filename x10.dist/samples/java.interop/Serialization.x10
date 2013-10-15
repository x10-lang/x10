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


public class Serialization {

    public static def main(args:Rail[String]):void {

        val y1 = 1y;
        val y2 = Runtime.deserializeByte(Runtime.serializeByte(y1));
        assert y1 == y2 : "y1 == y2";

        val s1 = 1s;
        val s2 = Runtime.deserializeShort(Runtime.serializeShort(s1));
        assert s1 == s2 : "s1 == s2";

        val i1 = 1;
        val i2 = Runtime.deserializeInt(Runtime.serializeInt(i1));
        assert i1 == i2 : "i1 == i2";

        val l1 = 1l;
        val l2 = Runtime.deserializeLong(Runtime.serializeLong(l1));
        assert l1 == l2 : "l1 == l2";

        val uy1 = 1uy;
        val uy2 = Runtime.deserializeUByte(Runtime.serializeUByte(uy1));
        assert uy1 == uy2 : "uy1 == uy2";

        val us1 = 1us;
        val us2 = Runtime.deserializeUShort(Runtime.serializeUShort(us1));
        assert us1 == us2 : "us1 == us2";

        val ui1 = 1u;
        val ui2 = Runtime.deserializeUInt(Runtime.serializeUInt(ui1));
        assert ui1 == ui2 : "ui1 == ui2";

        val ul1 = 1ul;
        val ul2 = Runtime.deserializeULong(Runtime.serializeULong(ul1));
        assert ul1 == ul2 : "ul1 == ul2";

        val f1 = 0.1f;
        val f2 = Runtime.deserializeFloat(Runtime.serializeFloat(f1));
        assert f1 == f2 : "f1 == f2";

        val d1 = 0.1d;
        val d2 = Runtime.deserializeDouble(Runtime.serializeDouble(d1));
        assert d1 == d2 : "d1 == d2";

        val c1 = '1';
        val c2 = Runtime.deserializeChar(Runtime.serializeChar(c1));
        assert c1 == c2 : "c1 == c2";

        val z1 = true;
        val z2 = Runtime.deserializeBoolean(Runtime.serializeBoolean(z1));
        assert z1 == z2 : "z1 == z2";

        val S1 = "1";
        val S2 = Runtime.deserialize(Runtime.serialize(S1)) as String;
        assert S1.equals(S2) : "S1.equals(S2)";

    }

}
