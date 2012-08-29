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


public abstract class MathUtils {
    
    public static double erf(double a) {
        return org.apache.commons.math3.special.Erf.erf(a);
    }
    
    public static double erfc(double a) {
        return org.apache.commons.math3.special.Erf.erfc(a);
    }
    
}
