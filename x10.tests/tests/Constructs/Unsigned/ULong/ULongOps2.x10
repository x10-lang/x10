/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import harness.x10Test;

/**
 * Test operations on ULongs.
 *
 * @author Salikh Zakirov 5/2011
 */
public class ULongOps2 extends x10Test {
    public def run() : boolean {
	val u1 = 1ul;
	val u2 = 2ul;
	assert 1ul + 2ul == 3ul;
	assert u1 + u2 == 3ul;
	assert u1 + u1 != 3ul;
	assert u1 == 1ul;
	assert u1 + 1n == 2ul;
	assert u1 + 1ul == 2ul;
	assert 1n + u2 == 3ul;
	assert 1ul + u2 == 3ul;
	assert u2 - u1 == 1ul;
	assert u2 - u1 == u1;
	assert u2 - 1ul == 1ul;
	assert u1 - u2 == (-1n as ULong);
	assert u1 - u2 == 0xFFFFffffFFFFfffful;
	assert u1 * u2 == u2;
	assert u2 * u1 == 2ul;
	assert 1n * u2 == 2ul;
	assert u2 * 1n == 2ul;
	assert u2 / u1 == 2ul;
	assert u1 / u2 == 0ul;
	assert u2 % u1 == 0ul;
	assert u1 % u2 == u1;
	assert u2 << 1n == 4ul;
	assert u2 >> 1n == 1ul;
	assert (u2 as Long) == 2l;
	assert (2 as ULong) == u2;
	assert (u2 as UShort) == 2su;
	assert (u2 as UByte) == 2yu;
	assert (u2 as ULong) == 2lu;
	assert (-u2) == (-2 as ULong);
	assert (-u1) == 0xFFFFffffFFFFfffful;
	assert 0ul < 1ul;
	assert 0ul < 0xFFFFffffFFFFfffful;
	assert (u1 | u2) == 3ul;
	assert (u1 & u2) == 0ul;
	assert (u1 ^ u2) == 3ul;
	return true;
    }


    public static def main(Rail[String]) {
        new ULongOps2().execute();
    }
}
