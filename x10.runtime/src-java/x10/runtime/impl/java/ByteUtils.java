/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.runtime.impl.java;


public abstract class ByteUtils {

    public static String toString(byte a, int radix) {
        if (a >= 0) {
            return java.lang.Integer.toString(a, radix);
        } else {
            int b = (0x80000000 - a) & 0x7FFFFFFF;
            return "-" + java.lang.Integer.toString(b, radix);
        }
    }

}
