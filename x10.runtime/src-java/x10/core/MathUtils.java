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
// TODO may need to be reimplemented as JNI wrapper for native error functions
public class MathUtils {
    private static java.lang.reflect.Method erf;
    static {
        try {
            Class<?> klass = Class.forName("org.apache.commons.math.special.Erf");
            erf = klass.getDeclaredMethod("erf", double.class);
        } catch (Exception e) {
        }
    }
    
    public static double erf(double a) {
        if (erf == null) {
            return ThrowableUtilities.<java.lang.Double> UnsupportedOperationException("x10.lang.Math.erf(Double):Double");
        }
        try {
            if (a == java.lang.Double.POSITIVE_INFINITY) return 1.0;
            if (a == java.lang.Double.NEGATIVE_INFINITY) return -1.0;
            return (java.lang.Double) erf.invoke(null, a);
        } catch (java.lang.Exception e) {
            throw ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }

    public static double erfc(double a) {
        if (erf == null) {
            return ThrowableUtilities.<java.lang.Double> UnsupportedOperationException("x10.lang.Math.erfc(Double):Double");
        }
        return 1 - erf(a);
    }
}
