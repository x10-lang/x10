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
	val u1 = 1u;
	val u2 = 2u;
	assert 1 + 2 == 3;
	assert u1 + u2 == 3u;
	assert u1 + u1 != 3u;
	assert u1 == 1u;
	assert u1 + 1 == 2u;
	assert u1 + 1u == 2u;
	assert 1 + u2 == 3;
	assert 1u + u2 == 3u;
	assert u2 - u1 == 1u;
	assert u2 - u1 == u1;
	assert u2 - 1u == 1u;
	assert u1 - u2 == (-1 as UInt);
	assert u1 - u2 == 0xffffffffu;
	assert u1 * u2 == u2;
	assert u2 * u1 == 2u;
	assert 1 * u2 == 2;
	assert u2 * 1 == 2u;
	assert u2 / u1 == 2u;
	assert u1 / u2 == 0u;
	assert u2 % u1 == 0u;
	assert u1 % u2 == u1;
	assert u2 << u1 == 4u;
	assert u2 >> u1 == 1u;
	assert (u2 as Int) == 2;
	assert (2 as UInt) == u2;
	assert (u2 as UShort) == 2su;
	assert (u2 as UByte) == 2yu;
	assert (u2 as ULong) == 2lu;
	assert (-u2) == (-2 as UInt);
	assert (-u1) == 0xffffffffu;
	assert 0u < 1u;
	assert 0u < 0xffffffffu;
	assert (u1 | u2) == 3u;
	assert (u1 & u2) == 0u;
	assert (u1 ^ u2) == 3u;
	return true;
    }


    public static def main(Rail[String]) {
        new UIntOps2().execute();
    }
}
