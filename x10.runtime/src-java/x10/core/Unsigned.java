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

public class Unsigned {
    public static long toLong(int a) {
        return a & 0xFFFFFFFFL;
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
        return (int) ( toLong(a) / toLong(b) );
    }
    public static int rem(int a, int b) {
        return (int) ( toLong(a) % toLong(b) );
    }

    public static long div(long a, long b) {
        if (a > 0 && b > 0)
            return a / b;
        else if (a < b)
            return 0;
        else
            return 1;
    }

    public static long rem(long a, long b) {
        if (a > 0 && b > 0)
            return a % b;
        else if (a < b)
            return a;
        else
            return a - b;
    }

    public static String toString(byte a) {
        return Integer.toString( a & 0xff );
    }
    public static String toString(short a) {
        return Integer.toString( a & 0xffff );
    }
    public static String toString(int a) {
        return Long.toString( toLong(a) );
    }
    public static String toString(long a) {
        StringBuilder sb = new StringBuilder();
        if (a >= 0)
            return Long.toString(a);
        while (a != 0) {
            char ch = (char) ('0' + a % 10);
            sb.append(ch);
            a /= 10;
        }
        return sb.reverse().toString();
    }
    
    private static String forInputString(String s) {
        return "For input string: \"" + s + "\"";
    }
    private static x10.core.Throwable newNumberFormatException(String s) {
        try {
            return Class.forName("x10.lang.NumberFormatException").asSubclass(x10.core.Throwable.class).getConstructor(new Class[] { String.class }).newInstance(new Object[] { s });
        } catch (java.lang.ClassNotFoundException e1) {
        } catch (java.lang.InstantiationException e2) {
        } catch (java.lang.IllegalAccessException e3) {
        } catch (java.lang.NoSuchMethodException e4) {
        } catch (java.lang.reflect.InvocationTargetException e5) {
        }
        return null;
    }
    
    private static final java.math.BigInteger ULONG_MAX = new java.math.BigInteger("ffffffffffffffff", 16);
    public static long parseULong(String s, int radix) {
        java.math.BigInteger bigint = new java.math.BigInteger(s, radix);
        if (bigint.signum() < 0) {
            throw newNumberFormatException(forInputString(s));
        }
        if (bigint.compareTo(ULONG_MAX) > 0) {
            throw newNumberFormatException(forInputString(s));
        }
        return bigint.longValue();
    }
    
}
