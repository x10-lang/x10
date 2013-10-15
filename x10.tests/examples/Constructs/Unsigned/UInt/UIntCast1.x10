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
 * Test casting of UInt to ULong, UByte
 *
 * @author Salikh Zakirov 5/2011
 */
public class UIntCast1 extends x10Test {
    public def run(): boolean = {
	val u1 = 1un;
	val b1 = 1uy;
	val l1 = 1ul;
	if (!(u1 == b1 as UInt)) return false;
	if (u1 != b1 as UInt) return false;
	if (!(u1 as UByte == b1)) return false;
	if (u1 as UByte != b1) return false;
	if (!(u1 == l1 as UInt)) return false;
	if (u1 != l1 as UInt) return false;
	if (!(u1 as ULong == l1)) return false;
	if (u1 as ULong != l1) return false;

	val u2 = 0xFFFFffffUN;
	val b2 = 0xffYU;
	val l2 = 0xFFFFffffLU;
	if (!(u2 as UByte == b2)) return false;
	if (u2 as UByte != b2) return false;
	if (!(u2 as ULong == l2)) return false;
	if (u2 as ULong != l2) return false;
	if (!(u2 == (0xFFFFffffFFFFffffLU as UInt))) return false;
	if (u2 != (0xFFFFffffFFFFffffLU as UInt)) return false;

	return true;
    }

    public static def main(Rail[String]) {
        new UIntCast1().execute();
    }
}
