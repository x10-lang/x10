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


public abstract class DoubleUtils {

    /*
     * Java does not have {f,d}2{b,s} bytecodes and implements conversions from {float,double} to {byte,short}
     * as {float,double} to int followed by int to {byte,short}.
     * Since int to {byte,short} conversions in Java do not handle overflows, we need the following functions
     * to implement semantics of X10 conversions correctly.
     */
    public static byte toByte(double a) {
        int ia = (int)a;
        if (ia > java.lang.Byte.MAX_VALUE) return java.lang.Byte.MAX_VALUE;
        else if (ia < java.lang.Byte.MIN_VALUE) return java.lang.Byte.MIN_VALUE;
        else return (byte)ia;
    }

    public static short toShort(double a) {
        int ia = (int)a;
        if (ia > java.lang.Short.MAX_VALUE) return java.lang.Short.MAX_VALUE;
        else if (ia < java.lang.Short.MIN_VALUE) return java.lang.Short.MIN_VALUE;
        else return (short)ia;
    }

    public static byte toUByte(double a) {
        int ia = (int)a;
        if (ia > 0xff) return (byte)0xff;
        else if (ia < 0) return 0;
        else return (byte)ia;
    }

    public static short toUShort(double a) {
        int ia = (int)a;
        if (ia > 0xffff) return (short)0xffff;
        else if (ia < 0) return 0;
        else return (short)ia;
    }

}
