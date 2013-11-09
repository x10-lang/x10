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
 * Test operations on UShorts.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UShortOps2 extends x10Test {
    public def run() : boolean {
	val u1 = 1us;
	val u2 = 2us;
	assert 1us + 2us == 3us;
	assert u1 + u2 == 3us;
	assert u1 + u1 != 3us;
	assert u1 == 1us;
	assert u1 + 1us == 2us;
	assert u1 + 1us == 2us;
	assert 1n + u2 == 3n;
	assert (1n + u2) as UShort == 3us;
	assert 1us + u2 == 3us;
	assert u2 - u1 == 1us;
	assert u2 - u1 == u1;
	assert u2 - 1us == 1us;
	assert u1 - u2 == (-1 as UShort);
	assert u1 - u2 == 0xFFFFus;
	assert u1 * u2 == u2;
	assert u2 * u1 == 2us;
	assert 1n * u2 == 2n;
	assert (1 * u2) as UShort == 2us;
	assert u2 * 1us == 2us;
	assert u2 / u1 == 2us;
	assert u1 / u2 == 0us;
	assert u2 % u1 == 0us;
	assert u1 % u2 == u1;
	assert u2 << 1n == 4us;
	assert u2 >> 1n == 1us;
	assert (u2 as Short) == 2s;
	assert (2 as UShort) == u2;
	assert (u2 as UShort) == 2su;
	assert (u2 as UByte) == 2yu;
	assert (u2 as UShort) == 2su;
	assert (-u2) == (-2 as UShort);
	assert (-u1) == 0xFFFFus;
	assert 0us < 1us;
	assert 0us < 0xFFFFus;
	assert (u1 | u2) == 3us;
	assert (u1 & u2) == 0us;
	assert (u1 ^ u2) == 3us;
	return true;
    }


    public static def main(Rail[String]) {
        new UShortOps2().execute();
    }
}
