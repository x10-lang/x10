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

public struct precision {
    public static val double_tolerance = 1.0e-14;
    public static val single_tolerance = 1.0e-5f;

    // this method defines a precision comparison with a tolerance
    public static def is_equal (a:Double, b:Double, tol:Double):Boolean {
        if (tol < 0.0) {
            x10.io.Console.OUT.println("Error in is_equal(Double,Double,Double): invalid tolerance "+tol);
            throw new Exception();
        }

        return Math.abs(a - b) <= 0.5d * Math.abs(a + b) * tol;
    }

    public static def is_equal (a:Double, b:Double):Boolean {
        return precision.is_equal(a, b, double_tolerance);
    }

    public static def is_equal (a:Float, b:Float, tol:Float):Boolean {
        if (tol < 0.0) {
            x10.io.Console.OUT.println("Error in is_equal(Float,Float,Float): invalid tolerance "+tol);
            throw new Exception();
        }

        return Math.abs(a - b) <= 0.5f * Math.abs(a + b) * tol;
    }

    public static def is_equal (a:Float, b:Float):Boolean {
        return precision.is_equal (a, b, single_tolerance);
    }

    public static def is_equal (a:Complex, b:Complex, tol:Double):Boolean {
        return precision.is_equal (a.re, b.re, tol) && precision.is_equal (a.im, b.im, tol);
    }

    public static def is_equal (a:Complex, b:Complex):Boolean {
        return precision.is_equal (a, b, single_tolerance); // default to lower precision
    }

    public static def is_equal_or_zero (a:Double, b:Double):Boolean {
        if ((Math.abs(a) <= double_tolerance) && (Math.abs(b) <= double_tolerance)) return true;

        return is_equal(a, b);
    }
}
