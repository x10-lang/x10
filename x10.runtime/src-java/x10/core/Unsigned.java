/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.core;

public abstract class Unsigned {
//    public static int toUInt(byte a) {
//        return a & 0xFF;
//    }
//    public static int toUInt(short a) {
//        return a & 0xFFFF;
//    }
//    public static long toULong(int a) {
//        return a & 0xFFFFFFFFL;
//    }
//    public static int toSInt(byte a) {
//        return a;
//    }
//    public static int toSInt(short a) {
//        return a;
//    }
//    public static long toSLong(int a) {
//        return a;
//    }
    
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

    public static int inject(int a) {
        return (a + java.lang.Integer.MIN_VALUE);
    }
    public static int deject(int a) {
        return (a - java.lang.Integer.MIN_VALUE);
    }
    public static long inject(long a) {
        return (a + java.lang.Long.MIN_VALUE);
    }
    public static long deject(long a) {
        return (a - java.lang.Long.MIN_VALUE);
    }

    public static boolean le(int a, int b) {
        return inject(a) <= inject(b);
    }
    public static boolean gt(int a, int b) {
        return inject(a) > inject(b);
    }
    public static boolean ge(int a, int b) {
        return inject(a) >= inject(b);
    }
    public static boolean lt(int a, int b) {
        return inject(a) < inject(b);
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
    
    public static int div(int a, int b) {
//        return (int) div(toULong(a), toULong(b));
//        return (int) (toULong(a) / toULong(b));
        return (int) ((a & 0xFFFFFFFFL) / (b & 0xFFFFFFFFL));
    }
//    public static int div_U_S(int a, int b) {
////        return (int) div_U_S(toULong(a), toSLong(b));
//        return (int) (toULong(a) / toSLong(b));     // need check
//    }
//    public static int div_S_U(int a, int b) {
////        return (int) div_S_U(toSLong(a), toULong(b));
//        return (int) (toSLong(a) / toULong(b));     // need check
//    }
    public static int rem(int a, int b) {
//        return (int) rem(toULong(a), toULong(b));
//        return (int) (toULong(a) % toULong(b));
        return (int) ((a & 0xFFFFFFFFL) % (b & 0xFFFFFFFFL));
    }
//    public static int rem_U_S(int a, int b) {
////        return (int) rem_U_S(toULong(a), toSLong(b));
//        return (int) (toULong(a) % toSLong(b));     // need check
//    }
//    public static int rem_S_U(int a, int b) {
////        return (int) rem_S_U(toSLong(a), toULong(b));
//        return (int) (toSLong(a) % toULong(b));     // need check
//    }

    public static long div(long a, long b) {
        try {
            if (a >= 0 && b >= 0)
                return a / b;
            else
                return toULONG(a).divide(toULONG(b)).longValue();
        } catch (ArithmeticException e) {
            throw ThrowableUtilities.getCorrespondingX10Throwable(e);
        }
    }
//    public static long div_U_S(long a, long b) {
//        try {
//            if (a >= 0 && b >= 0)
//                return a / b;
//            else
//                return toULONG(a).divide(toSLONG(b)).longValue();
//        } catch (ArithmeticException e) {
//            throw ThrowableUtilities.getCorrespondingX10Throwable(e);
//        }
//    }
//    public static long div_S_U(long a, long b) {
//        try {
//            if (a >= 0 && b >= 0)
//                return a / b;
//            else
//                return toSLONG(a).divide(toULONG(b)).longValue();
//        } catch (ArithmeticException e) {
//            throw ThrowableUtilities.getCorrespondingX10Throwable(e);
//        }
//    }
    public static long rem(long a, long b) {
        try {
            if (a >= 0 && b >= 0)
                return a % b;
            else
                return toULONG(a).remainder(toULONG(b)).longValue();
        } catch (ArithmeticException e) {
            throw ThrowableUtilities.getCorrespondingX10Throwable(e);
        }
    }
//    public static long rem_U_S(long a, long b) {
//        try {
//            if (a >= 0 && b >= 0)
//                return a % b;
//            else
//                return toULONG(a).remainder(toSLONG(b)).longValue();
//        } catch (ArithmeticException e) {
//            throw ThrowableUtilities.getCorrespondingX10Throwable(e);
//        }
//    }
//    public static long rem_S_U(long a, long b) {
//        try {
//            if (a >= 0 && b >= 0)
//                return a % b;
//            else
//                return toSLONG(a).remainder(toULONG(b)).longValue();
//        } catch (ArithmeticException e) {
//            throw ThrowableUtilities.getCorrespondingX10Throwable(e);
//        }
//    }
    
    private static java.lang.String forInputString(java.lang.String s) {
        return "For input string: \"" + s + "\"";
    }
    private static x10.core.Throwable newNumberFormatException(java.lang.String s) {
        try {
            return Class.forName("x10.lang.NumberFormatException").asSubclass(x10.core.Throwable.class).getConstructor(new Class[] { java.lang.String.class }).newInstance(new Object[] { s });
        } catch (java.lang.ClassNotFoundException e1) {
        } catch (java.lang.InstantiationException e2) {
        } catch (java.lang.IllegalAccessException e3) {
        } catch (java.lang.NoSuchMethodException e4) {
        } catch (java.lang.reflect.InvocationTargetException e5) {
        }
        return null;
    }
    
//    private static java.math.BigInteger toSLONG(long a) {
//        byte[] bytes = new byte[8];
//        for (int i = 0; i < 8; ++i) {
//            bytes[8 - 1 - i] = (byte)(a & 0xff);
//            a >>= 8;
//        }
//        return new java.math.BigInteger(bytes);        
//    }
    private static java.math.BigInteger toULONG(long a) {
        byte[] bytes = new byte[9]; // set zero to bytes[0] to make the value positive
        for (int i = 0; i < 8; ++i) {
            bytes[9 - 1 - i] = (byte)(a & 0xff);
            a >>= 8;
        }
        return new java.math.BigInteger(bytes);        
    }

    
    private static final java.math.BigInteger ULONG_MAX = toULONG(0xFFFFFFFFFFFFFFFFL);
    public static long parseULong(java.lang.String s, int radix) {
        java.math.BigInteger ulong = new java.math.BigInteger(s, radix);
        if (ulong.signum() < 0) {
            throw newNumberFormatException(forInputString(s));
        }
        if (ulong.compareTo(ULONG_MAX) > 0) {
            throw newNumberFormatException(forInputString(s));
        }
        return ulong.longValue();
    }
    public static long parseULong(java.lang.String s) {
        return parseULong(s, 10);
    }
    
    /*
    // followings are correct but not used
    public static java.lang.String toString(byte a, int radix) {
        return Integer.toString(toUInt(a), radix);
    }
    public static java.lang.String toString(byte a) {
        return toString(a, 10);
    }
    public static java.lang.String toString(short a, int radix) {
        return Integer.toString(toUInt(a), radix);
    }
    public static java.lang.String toString(short a) {
        return toString(a, 10);
    }
    public static java.lang.String toString(int a, int radix) {
        return java.lang.Long.toString(toULong(a), radix);
    }
    public static java.lang.String toString(int a) {
        return toString(a, 10);
    }
    */
    public static java.lang.String toString(long a, int radix) {
        return toULONG(a).toString(radix);
    }
    public static java.lang.String toString(long a) {
        return toULONG(a).toString();
    }
    
}
