/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */

import harness.x10Test;

/**
 * Test x10.lang.Math operations for integral operands.
 */
class TestIntegerMath extends x10Test {
    public def run(): boolean {
        testInt();
        testLong();

        return true;
    }

    public def testInt() {
        val two = Math.pow(Math.sqrt(2n), 2n);
        chk (precision.is_equal(two, 2.0));

        val four = Math.pow(2n, 2.0);
        chk (four == 4.0);
        val fourAgain = Math.pow(2n, 2n);
        chk (fourAgain == 4.0);

        val ten = Math.exp(Math.log(10n));
        chk (precision.is_equal(ten, 10.0));
    }

    public def testLong() {
        val two = Math.pow(Math.sqrt(2), 2);
        chk (precision.is_equal(two, 2.0));

        val four = Math.pow(2, 2.0);
        chk (four == 4.0);
        val fourAgain = Math.pow(2, 2);
        chk (fourAgain == 4.0);

        val ten = Math.exp(Math.log(10));
        chk (precision.is_equal(ten, 10.0));
    }

    public static def main(Rail[String]) {
        new TestIntegerMath().execute();
    }
}

