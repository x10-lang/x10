/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2009-2010.
 * @author milthorpe
 */
import harness.x10Test;

public class TestByteToString extends x10Test { 
    public def run() {
        val a = 42 as Byte;
        chk("42".equals(a.toString()));
        chk("101010".equals(a.toBinaryString()));
        chk("52".equals(a.toOctalString()));
        chk("2a".equals(a.toHexString()));

        chk(a == Byte.parse("42"));
        chk(a == Byte.parse("101010", 2));
        chk(a == Byte.parse("52", 8));
        chk(a == Byte.parse("2a", 16));

        val b = -23 as Byte;
        chk("-23".equals(b.toString()));
        chk("-10111".equals(b.toBinaryString()));;
        chk("-27".equals(b.toOctalString()));
        chk("-17".equals(b.toHexString()));

        chk(b == Byte.parse("-23"));
        chk(b == Byte.parse("-10111", 2));
        chk(b == Byte.parse("-27", 8));
        chk(b == Byte.parse("-17", 16));

        val c = 127 as Byte;
        chk("127".equals(c.toString()));
        chk("1111111".equals(c.toBinaryString()));
        chk("177".equals(c.toOctalString()));
        chk("7f".equals(c.toHexString()));

        chk(c == Byte.parse("127"));
        chk(c == Byte.parse("1111111", 2));
        chk(c == Byte.parse("177", 8));
        chk(c == Byte.parse("7f", 16));

        val d = -128 as Byte;
        chk("-128".equals(d.toString()));
        chk("-10000000".equals(d.toBinaryString()));
        chk("-200".equals(d.toOctalString()));
        chk("-80".equals(d.toHexString()));

        chk(d == Byte.parse("-128"));
        chk(d == Byte.parse("-10000000", 2));
        chk(d == Byte.parse("-200", 8));
        chk(d == Byte.parse("-80", 16));

        return true;
    }

    public static def main(Array[String](1)) {
        new TestByteToString().execute();
    }
}
