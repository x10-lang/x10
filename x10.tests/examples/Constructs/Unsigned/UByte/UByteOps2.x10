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
 * Test operations on UBytes.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UByteOps2 extends x10Test {
    public def run() : boolean {
	val u1 = 1uy;
	val u2 = 2uy;
	assert 1uy + 2uy == 3uy;
	assert u1 + u2 == 3uy;
	assert u1 + u1 != 3uy;
	assert u1 == 1uy;
	assert u1 + 1uy == 2uy;
	assert u1 + 1uy == 2uy;
	assert ((1 + u2) as UByte) == 3uy;
	assert 1 + u2 == 3;
	assert 1uy + u2 == 3uy;
	assert u2 - u1 == 1uy;
	assert u2 - u1 == u1;
	assert u2 - 1uy == 1uy;
	assert u1 - u2 == (-1 as UByte);
	assert u1 - u2 == 0xFFuy;
	assert u1 * u2 == u2;
	assert u2 * u1 == 2uy;
	assert 1 * u2 == 2;
	assert (1 * u2) as UByte == 2uy;
	assert u2 * 1uy == 2uy;
	assert u2 / u1 == 2uy;
	assert u1 / u2 == 0uy;
	assert u2 % u1 == 0uy;
	assert u1 % u2 == u1;
	assert u2 << 1n == 4uy;
	assert u2 >> 1n == 1uy;
	assert (u2 as Byte) == 2y;
	assert (2 as UByte) == u2;
	assert (u2 as UShort) == 2su;
	assert (u2 as UByte) == 2yu;
	assert (u2 as UByte) == 2yu;
	assert (-u2) == (-2 as UByte);
	assert (-u1) == 0xFFuy;
	assert 0uy < 1uy;
	assert 0uy < 0xFFuy;
	assert (u1 | u2) == 3uy;
	assert (u1 & u2) == 0uy;
	assert (u1 ^ u2) == 3uy;
	return true;
    }


    public static def main(Rail[String]) {
        new UByteOps2().execute();
    }
}
