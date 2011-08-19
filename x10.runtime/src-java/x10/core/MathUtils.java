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

public abstract class MathUtils {
    
    public static double erf(double a) {
        try {
            return org.apache.commons.math.special.Erf.erf(a);
        } catch (org.apache.commons.math.MathException e) {
            throw ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
    
    public static double erfc(double a) {
        return 1.0 - erf(a);
    }
    
}
