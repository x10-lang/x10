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
 * Test casting of Byte to UByte
 *
 * @author Salikh Zakirov 5/2011
 */
public class UByteCast0 extends x10Test {
    public def run(): boolean = {
	val xx = 1uy;
	val i1 = 1n;
	val l1 = 1y;
	val b1 = 1y;
	if (!(xx as Byte == l1)) return false;
	if (xx != i1 as UByte) return false;
	if (!((xx as Byte) as Int == i1)) return false;
	if (xx != l1 as UByte) return false;
	if (!((xx as Byte) as Byte == b1)) return false;
	if (xx != b1 as UByte) return false;

	val yy = 0xFFuy;
	val i2 = -1n;
	val l2 = -1y;
	val b2 = -1y;
	if (!(yy as Byte == l2)) return false;
	if (yy != l2 as UByte) return false;
	if (!((yy as Byte) as Int == i2)) return false;
	if (yy != l2 as UByte) return false;
	if (!((yy as Byte) as Byte == b2)) return false;
	if (yy != b2 as UByte) return false;

	return true;
    }

    public static def main(Rail[String]) {
        new UByteCast0().execute();
    }
}
