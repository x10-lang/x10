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
 * Test operations on UInts.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UIntOps2 extends x10Test {
    public def run() : boolean {
	val u1 = 1un;
	val u2 = 2un;
	assert 1n + 2n == 3n;
	assert u1 + u2 == 3un;
	assert u1 + u1 != 3un;
	assert u1 == 1un;
	assert u1 + 1un == 2un;
	assert u1 + 1un == 2un;
	assert 1un + u2 == 3un;
	assert 1un + u2 == 3un;
	assert u2 - u1 == 1un;
	assert u2 - u1 == u1;
	assert u2 - 1un == 1un;
	assert u1 - u2 == (-1un as UInt);
	assert u1 - u2 == 0xffffffffun;
	assert u1 * u2 == u2;
	assert u2 * u1 == 2un;
	assert 1un * u2 == 2un;
	assert u2 * 1un == 2un;
	assert u2 / u1 == 2un;
	assert u1 / u2 == 0un;
	assert u2 % u1 == 0un;
	assert u1 % u2 == u1;
	assert u2 << (u1 as Int) == 4un;
	assert u2 >> (u1 as Int) == 1un;
	assert (u2 as Int) == 2n;
	assert (2 as UInt) == u2;
	assert (u2 as UShort) == 2su;
	assert (u2 as UByte) == 2yu;
	assert (u2 as ULong) == 2lu;
	assert (-u2) == (-2 as UInt);
	assert (-u1) == 0xffffffffun;
	assert 0u < 1u;
	assert 0u < 0xffffffffun;
	assert (u1 | u2) == 3un;
	assert (u1 & u2) == 0un;
	assert (u1 ^ u2) == 3un;
	return true;
    }


    public static def main(Rail[String]) {
        new UIntOps2().execute();
    }
}
