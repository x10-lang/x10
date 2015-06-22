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
 * Test casting of Int to UInt
 *
 * @author Salikh Zakirov 5/2011
 */
public class UIntCast0 extends x10Test {
    public def run(): boolean {
	val u1 = 1un;
	val i1 = 1n;
	val l1 = 1l;
	val b1 = 1y;
	if (!(u1 as Int == i1)) return false;
	if (u1 != i1 as UInt) return false;
	if (!((u1 as Int) as Long == l1)) return false;
	if (u1 != l1 as UInt) return false;
	if (!((u1 as Int) as Byte == b1)) return false;
	if (u1 != b1 as UInt) return false;

	val u2 = 0xFFFFffffUN;
	val i2 = -1n;
	val l2 = -1l;
	val b2 = -1y;
	if (!(u2 as Int == i2)) return false;
	if (u2 != i2 as UInt) return false;
	if (!((u2 as Int) as Long == l2)) return false;
	if (u2 != l2 as UInt) return false;
	if (!((u2 as Int) as Byte == b2)) return false;
	if (u2 != b2 as UInt) return false;

	return true;
    }

    public static def main(Rail[String]) {
        new UIntCast0().execute();
    }
}
