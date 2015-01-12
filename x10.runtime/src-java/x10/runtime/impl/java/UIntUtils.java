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


public abstract class UIntUtils {

    public static int inject(int a) {
        return (a + java.lang.Integer.MIN_VALUE);
    }
    public static int deject(int a) {
        return (a - java.lang.Integer.MIN_VALUE);
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

    public static int div(int a, int b) {
        return (int) ((a & 0xFFFFFFFFL) / (b & 0xFFFFFFFFL));
    }
    public static int rem(int a, int b) {
        return (int) ((a & 0xFFFFFFFFL) % (b & 0xFFFFFFFFL));
    }

}
