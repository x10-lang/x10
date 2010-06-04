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

public class UnaryPost {
    public static byte beforeIncrement(byte t) { return (byte)(t - 1); }
    public static byte beforeDecrement(byte t) { return (byte)(t + 1); }
    public static short beforeIncrement(short t) { return (short)(t - 1); }
    public static short beforeDecrement(short t) { return (short)(t + 1); }
    public static int beforeIncrement(int t) { return t - 1; }
    public static int beforeDecrement(int t) { return t + 1; }
    public static long beforeIncrement(long t) { return t - 1L; }
    public static long beforeDecrement(long t) { return t + 1L; }
    public static float beforeIncrement(float t) { return t - 1.0F; }
    public static float beforeDecrement(float t) { return t + 1.0F; }
    public static double beforeIncrement(double t) { return t - 1.0; }
    public static double beforeDecrement(double t) { return t + 1.0; }
}
