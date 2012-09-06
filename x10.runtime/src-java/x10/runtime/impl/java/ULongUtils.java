/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.runtime.impl.java;


public abstract class ULongUtils {

    /**
     * Converts a ULong to double
     * @param a unboxed ULong value represented as long
     * @return value converted to double
     */
    public static double toDouble(long a) {
        double v = (double)a;
        if (a >= 0) return v;
        else return v - 2.0*java.lang.Long.MIN_VALUE;
    }

    /**
     * Converts a ULong to float
     * @param a unboxed ULong value represented as long
     * @return value converted to float
     */
    public static float toFloat(long a) {
        double v = (double)a;
        if (a >= 0) return (float)v;
        else return (float)(v - 2.0*java.lang.Long.MIN_VALUE);
    }

}
