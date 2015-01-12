/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *  (C) Copyright Australian National University 2009-2010.
 */

import harness.x10Test;

/**
 * @author milthorpe 6/2009
 */
class TestMathTrig extends x10Test {

    public def run(): boolean {
        /*
         * Validate x10.lang.Math functions by use of trigonometric 
         * and hyperbolic identities.
         */
        val theta : double = Math.PI / 4.0;
        val cos : double = Math.cos(theta);
        val sin : double = Math.sin(theta);
        val tan : double = Math.tan(theta);
        chk(precision.is_equal(tan, sin / cos));

        var theta2 : double = Math.atan(tan);
        chk(precision.is_equal(theta2, theta));

        val x : double = 0.6;
        chk(precision.is_equal(Math.asin(x) + Math.acos(x), Math.PI / 2.0));
        chk(precision.is_equal(Math.atan(x) + Math.atan(1.0/x), Math.PI / 2.0));

        val sinh : double = Math.sinh(theta);
        val cosh : double = Math.cosh(theta);
        val tanh : double = Math.tanh(theta);
        chk(precision.is_equal(tanh, sinh / cosh));   

        /* cartesian (1.0, 1.0) = polar (PI/4, sqrt(2)) */ 
        theta2 = Math.atan2(1.0, 1.0);
        chk(precision.is_equal(theta2, theta));
        val hypot : double = Math.hypot(1.0, 1.0);
        chk(precision.is_equal(hypot, Math.sqrt(2.0)));     

        /* Validate cube root against a cube */
        val piCubed : double = Math.PI * Math.PI * Math.PI;
        chk (precision.is_equal(Math.cbrt(piCubed), Math.PI));

        /* Validate expm1 against log1p */
        val d : double = 0.02;
        val e : double = Math.expm1(d);
        chk(precision.is_equal(Math.log1p(e), d));

        return true;
    }

    public static def main(Rail[String]) {
        new TestMathTrig().execute();
    }
}

