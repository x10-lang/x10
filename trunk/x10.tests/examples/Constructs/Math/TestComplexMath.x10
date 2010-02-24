/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
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
        chk(Math.pow(a, z) == Math.exp(z * Math.log(a)));

        val sinZ = Math.sin(z);
        val cosZ = Math.cos(z);

        // sin(z1 + z2) = sin(z1)cos(z2) + cos(z1)sin(z2)
        chk(nearEnough(Math.sin(a + z), Math.sin(a) * cosZ + Math.cos(a) * sinZ));
        
        val tanZ = Math.tan(z);
        // tan(z) = sin(z) / cos(z)
        chk(nearEnough(tanZ, sinZ / cosZ));

        val secZ = 1.0 / cosZ;
        // tan2(z) + 1 = sec2(z)
        chk(nearEnough(secZ * secZ, tanZ * tanZ + 1.0));

        val cotZ = 1.0 / tanZ;
        val cosecZ = 1.0 / sinZ;
        // cot2(z) + 1 = csc2(z)
        chk(nearEnough(cosecZ * cosecZ, cotZ * cotZ + 1.0));

        // inverse trigonometric identities
        // note these won't always hold as inverse trig are all multi-valued
        chk(nearEnough(Math.asin(sinZ), z));
        chk(nearEnough(Math.acos(cosZ), z));
        chk(nearEnough(Math.atan(tanZ), z));

        val sinhZ = Math.sinh(z);
        val coshZ = Math.cosh(z);
        val tanhZ = Math.tanh(z);
        // tanh(z) = sinh(z) / cosh(z)
        chk(nearEnough(tanhZ, sinhZ / coshZ));

        // sinh(z1 + z2) = sinh(z1)cosh(z2) + cosh(z1)sinh(z2)
        chk(nearEnough(Math.sinh(a + z), Math.sinh(a) * Math.cosh(z) + Math.cosh(a) * Math.sinh(z)));

        return true;
    }

    /**
     * Returns true if a and b are near-enough equal.
     * This sort of check is necessary because complex identities
     * implemented in cartesian form in double precision are usually
     * inaccurate in the last digit. 
     */
    private static def nearEnough(a : Complex, b : Complex) {
        return ((a.re as Float) == (b.re as Float) 
             && (a.im as Float) == (b.im as Float));
    }

    public static def main(Rail[String]) {
        new TestComplexMath().execute();
    }

}
