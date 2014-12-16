/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2009-2010.
 */

import harness.x10Test;

/**
 * Test x10.lang.Math methods for complex operands.
 * @author milthorpe
 */
class TestComplexMath extends x10Test {
    public def run(): boolean {
        val a = Complex(1.0, -3.1);
        val z = Complex(0.5, 2.0);

        // a^z = e^(z ln a)
        chk(precision.is_equal(Math.pow(a, z), Math.exp(z * Math.log(a))));

        val sinZ = Math.sin(z);
        val cosZ = Math.cos(z);

        // sin(z1 + z2) = sin(z1)cos(z2) + cos(z1)sin(z2)
        chk(precision.is_equal(Math.sin(a + z), Math.sin(a) * cosZ + Math.cos(a) * sinZ));
        
        val tanZ = Math.tan(z);
        // tan(z) = sin(z) / cos(z)
        chk(precision.is_equal(tanZ, sinZ / cosZ));

        val secZ = 1.0 / cosZ;
        // tan2(z) + 1 = sec2(z)
        chk(precision.is_equal(secZ * secZ, tanZ * tanZ + 1.0));

        val cotZ = 1.0 / tanZ;
        val cosecZ = 1.0 / sinZ;
        // cot2(z) + 1 = csc2(z)
        chk(precision.is_equal(cosecZ * cosecZ, cotZ * cotZ + 1.0));

        // inverse trigonometric identities
        // note these won't always hold as inverse trig are all multi-valued
        chk(precision.is_equal(Math.asin(sinZ), z));
        chk(precision.is_equal(Math.acos(cosZ), z));
        chk(precision.is_equal(Math.atan(tanZ), z));

        val sinhZ = Math.sinh(z);
        val coshZ = Math.cosh(z);
        val tanhZ = Math.tanh(z);
        // tanh(z) = sinh(z) / cosh(z)
        chk(precision.is_equal(tanhZ, sinhZ / coshZ));

        // sinh(z1 + z2) = sinh(z1)cosh(z2) + cosh(z1)sinh(z2)
        chk(precision.is_equal(Math.sinh(a + z), Math.sinh(a) * Math.cosh(z) + Math.cosh(a) * Math.sinh(z)));

        return true;
    }

    public static def main(Rail[String]) {
        new TestComplexMath().execute();
    }

}
