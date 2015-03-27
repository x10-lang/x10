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
 * Test casting of ULong to ULong, UByte
 *
 * @author Salikh Zakirov 5/2011
 */
public class ULongCast1 extends x10Test {
    public def run(): boolean {
	val xx = 1ul;
	val b1 = 1uy;
	val i1 = 1un;
	if (!(xx == b1 as ULong)) return false;
	if (xx != b1 as ULong) return false;
	if (!(xx as UByte == b1)) return false;
	if (xx as UByte != b1) return false;
	if (!(xx == i1 as ULong)) return false;
	if (xx != i1 as ULong) return false;
	if (!(xx as UInt == i1)) return false;
	if (xx as UInt != i1) return false;

	val yy = 0xFFFFffffFFFFfffful;
	val b2 = 0xffYU;
	val u2 = 0xFFFFffffUN;
	if (!(yy as UByte == b2)) return false;
	if (yy as UByte != b2) return false;
	if (!(yy as UInt == u2)) return false;
	if (yy as UInt != u2) return false;
	if (!(yy == -1l as ULong)) return false;
	if (yy != -1l as ULong) return false;

	return true;
    }

    public static def main(Rail[String]) {
        new ULongCast1().execute();
    }
}
