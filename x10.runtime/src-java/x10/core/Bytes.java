/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2009-2010.
 *
 * @author milthorpe
 */
package x10.core;

public class Bytes {
    public static String toString(byte a, int radix) {
        if (a >= 0) {
            return Integer.toString(a, radix);
        } else {
            int b = (0x80000000 - a) & 0x7FFFFFFF;
            return "-" + Integer.toString(b, radix);
        }
    }
}
