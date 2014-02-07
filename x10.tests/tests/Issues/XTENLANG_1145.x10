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

/**
 * @author mtake 03/2010
 */

public class XTENLANG_1145 extends x10Test {

    public def run():boolean {
        var result:boolean = true;

        result &= (Float.MAX_VALUE as Byte) == Byte.MAX_VALUE;
        result &= (Float.MAX_VALUE as Byte).toString().equals("127");
        result &= ((-Float.MAX_VALUE) as Byte) == Byte.MIN_VALUE;
        result &= ((-Float.MAX_VALUE) as Byte).toString().equals("-128");
        result &= (Double.MAX_VALUE as Byte) == Byte.MAX_VALUE;
        result &= (Double.MAX_VALUE as Byte).toString().equals("127");
        result &= ((-Double.MAX_VALUE) as Byte) == Byte.MIN_VALUE;
        result &= ((-Double.MAX_VALUE) as Byte).toString().equals("-128");

        result &= (Float.MAX_VALUE as Short) == Short.MAX_VALUE;
        result &= (Float.MAX_VALUE as Short).toString().equals("32767");
        result &= ((-Float.MAX_VALUE) as Short) == Short.MIN_VALUE;
        result &= ((-Float.MAX_VALUE) as Short).toString().equals("-32768");
        result &= (Double.MAX_VALUE as Short) == Short.MAX_VALUE;
        result &= (Double.MAX_VALUE as Short).toString().equals("32767");
        result &= ((-Double.MAX_VALUE) as Short) == Short.MIN_VALUE;
        result &= ((-Double.MAX_VALUE) as Short).toString().equals("-32768");

        result &= (Float.MAX_VALUE as UByte) == UByte.MAX_VALUE;
        result &= (Float.MAX_VALUE as UByte).toString().equals("255");
        result &= ((-Float.MAX_VALUE) as UByte) == UByte.MIN_VALUE;
        result &= ((-Float.MAX_VALUE) as UByte).toString().equals("0");
        result &= (Double.MAX_VALUE as UByte) == UByte.MAX_VALUE;
        result &= (Double.MAX_VALUE as UByte).toString().equals("255");
        result &= ((-Double.MAX_VALUE) as UByte) == UByte.MIN_VALUE;
        result &= ((-Double.MAX_VALUE) as UByte).toString().equals("0");

        result &= (Float.MAX_VALUE as UShort) == UShort.MAX_VALUE;
        result &= (Float.MAX_VALUE as UShort).toString().equals("65535");
        result &= ((-Float.MAX_VALUE) as UShort) == UShort.MIN_VALUE;
        result &= ((-Float.MAX_VALUE) as UShort).toString().equals("0");
        result &= (Double.MAX_VALUE as UShort) == UShort.MAX_VALUE;
        result &= (Double.MAX_VALUE as UShort).toString().equals("65535");
        result &= ((-Double.MAX_VALUE) as UShort) == UShort.MIN_VALUE;
        result &= ((-Double.MAX_VALUE) as UShort).toString().equals("0");

        return result;
    }

    public static def main(Rail[String]) {
        new XTENLANG_1145().execute();
    }

}
