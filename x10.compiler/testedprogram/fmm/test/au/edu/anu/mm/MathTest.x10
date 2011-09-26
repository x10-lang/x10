/*
 * This file is part of ANUChem.
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * (C) Copyright Josh Milthorpe 2010.
 */
package au.edu.anu.mm;

import harness.x10Test;

/**
 * Superclass for tests that require comparison of Complex and Double
 * @author milthorpe
 */
abstract class MathTest extends x10Test {

    public def nearlyEqual(a : Complex, b : Complex, maxRelativeError : Double, maxAbsoluteError : Double) : Boolean {
        if (a == b)
            return true;

        return nearlyEqual(a.re, b.re, maxRelativeError, maxAbsoluteError) 
            && nearlyEqual(a.im, b.im, maxRelativeError, maxAbsoluteError);
    }

    public def nearlyEqual(a: Double, b: Double, maxRelativeError : Double, maxAbsoluteError : Double) : Boolean {
        if (a == b)
            return true;

        if (Math.abs(a - b) < maxAbsoluteError)
            return true;

        var relativeError : Double;

        if (Math.abs(b) > Math.abs(a))
            relativeError = Math.abs((a - b) / b);
        else
            relativeError = Math.abs((a - b) / a);

        if (relativeError <= maxRelativeError)
            return true;
        else
            return false;
    }
}
