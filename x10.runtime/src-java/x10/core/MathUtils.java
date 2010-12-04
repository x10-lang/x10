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

// XTENLANG-910
// TODO to be implemented as JNI wrapper for native error functions
public class MathUtils {
    public static double erf(double a) {
        return ThrowableUtilities.<java.lang.Double> UnsupportedOperationException("x10.lang.Math.erf(Double):Double");
    }
    public static double erfc(double a) {
        return ThrowableUtilities.<java.lang.Double> UnsupportedOperationException("x10.lang.Math.erfc(Double):Double");
    }
}
