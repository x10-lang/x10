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
 * Test operations on complex numbers.
 * @author milthorpe
 */
class TestComplex extends x10Test {
    public def run(): boolean {
        val a = Complex(2.0, 2.0);
        chk (precision.is_equal((-(-a)), a));
        chk (precision.is_equal(a.abs(), Math.sqrt(8.0)));

        val b = a.conjugate();
        chk (precision.is_equal(b.conjugate(), a));
        chk (Complex.NaN.conjugate().isNaN());

        val c = Complex(1.0, 4.0);
        chk (precision.is_equal(a + c - c, a), "a + c - c = a");
        /* Note: this identity does not always hold, given peculiarities of Smith's algorithm for division */
        chk (precision.is_equal((a * c) / c, a), "a * c / c = a");
        

        val d = Complex(4.0, -1.0);
        chk (precision.is_equal(a + d - d, a), "a + d - d = a");
        
        chk (precision.is_equal((a * d) / d, a), "a * d / d = a");

        val e = a / Complex(0.0, 0.0);
        chk (e.isNaN());

        val f = Complex(3.0, 4.0);
        val fAbs = f.abs();
        chk (precision.is_equal(fAbs, 5.0)); // abs(3 + 4i) = 5 by Pythagoras' theorem

        return true;
    }

    public static def main(Array[String](1)) {
        new TestComplex().execute();
    }

}
