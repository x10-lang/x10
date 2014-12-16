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

import harness.x10Test;

public class TypeName1 extends x10Test {
    public def run():Boolean {
        chk("x10.lang.Byte".equals(1y.typeName()));
        chk("x10.lang.Short".equals(1s.typeName()));
        chk("x10.lang.Int".equals(1n.typeName()));
        chk("x10.lang.Long".equals(1l.typeName()));
        chk("x10.lang.UByte".equals(1uy.typeName()));
        chk("x10.lang.UShort".equals(1us.typeName()));
        chk("x10.lang.UInt".equals(1un.typeName()));
        chk("x10.lang.ULong".equals(1ul.typeName()));
        chk("x10.lang.Float".equals(1.0f.typeName()));
        chk("x10.lang.Double".equals(1.0.typeName()));
        chk("x10.lang.Char".equals('a'.typeName()));
        chk("x10.lang.Boolean".equals(false.typeName()));
        chk("x10.lang.String".equals("".typeName()));
        return true;
    }
    public static def main(args:Rail[String]) {
        new TypeName1().execute();
    }
}
