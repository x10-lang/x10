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

package x10.runtime.impl.java;


public abstract class ULongUtils {

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

    public static long inject(long a) {
        return (a + java.lang.Long.MIN_VALUE);
    }
    public static long deject(long a) {
        return (a - java.lang.Long.MIN_VALUE);
    }

    public static boolean le(long a, long b) {
        return inject(a) <= inject(b);
    }
    public static boolean gt(long a, long b) {
        return inject(a) > inject(b);
    }
    public static boolean ge(long a, long b) {
        return inject(a) >= inject(b);
    }
    public static boolean lt(long a, long b) {
        return inject(a) < inject(b);
    }

    private static java.math.BigInteger toULONG(long a) {
        byte[] bytes = new byte[9]; // set zero to bytes[0] to make the value positive
        for (int i = 0; i < 8; ++i) {
            bytes[9 - 1 - i] = (byte)(a & 0xff);
            a >>= 8;
        }
        return new java.math.BigInteger(bytes);        
    }
    private static final java.math.BigInteger ULONG_MAX = toULONG(0xFFFFFFFFFFFFFFFFL);

    public static long div(long a, long b) {
        if (a >= 0 && b >= 0)
            return a / b;
        else
            return toULONG(a).divide(toULONG(b)).longValue();
    }
    public static long rem(long a, long b) {
        if (a >= 0 && b >= 0)
            return a % b;
        else
            return toULONG(a).remainder(toULONG(b)).longValue();
    }

    public static long parseULong(String s, int radix) {
        java.math.BigInteger ulong = new java.math.BigInteger(s, radix);
        if (ulong.signum() < 0 || ulong.compareTo(ULONG_MAX) > 0) {
            throw new java.lang.NumberFormatException("For input string: \"" + s + "\"");
        }
        return ulong.longValue();
    }
    public static long parseULong(String s) {
        return parseULong(s, 10);
    }

    public static String toString(long a, int radix) {
        return toULONG(a).toString(radix);
    }
    public static String toString(long a) {
        return toULONG(a).toString();
    }

}
