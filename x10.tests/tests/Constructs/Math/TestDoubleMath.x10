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
 * Test x10.lang.Math operations for Double operands.
 * @author milthorpe
 */
class TestDoubleMath extends x10Test {
    public def run(): boolean {
        // error function
        val a = 0.6;
        chk (precision.is_equal(Math.erf(a), 1.0 - Math.erfc(a)));

        chk (Math.erf(0) == 0.0);
        chk (Math.erfc(0) == 1.0);
        chk (Math.erf(Double.POSITIVE_INFINITY) == 1.0);
        chk (Math.erfc(Double.POSITIVE_INFINITY) == 0.0);

        return true;
    }

    public static def main(Rail[String]) {
        new TestDoubleMath().execute();
    }

}
