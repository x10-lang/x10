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


public abstract class EvalUtils {
    // Helper function to convert any expression to valid Java statement by swalloing its value
    // Fast implementation without boxing primitives
    public static void eval(Object expr) {}
    public static void eval(boolean expr) {}
    public static void eval(char expr) {}
    public static void eval(byte expr) {}
    public static void eval(short expr) {}
    public static void eval(int expr) {}
    public static void eval(long expr) {}
    public static void eval(float expr) {}
    public static void eval(double expr) {}
}
