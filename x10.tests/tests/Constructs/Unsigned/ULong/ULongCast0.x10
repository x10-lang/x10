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
 * Test casting of Long to ULong
 *
 * @author Salikh Zakirov 5/2011
 */
public class ULongCast0 extends x10Test {
    public def run(): boolean = {
	val xx = 1ul;
	val i1 = 1n;
	val l1 = 1l;
	val b1 = 1y;
	if (!(xx as Long == l1)) return false;
	if (xx != i1 as ULong) return false;
	if (!((xx as Long) as Int == i1)) return false;
	if (xx != l1 as ULong) return false;
	if (!((xx as Long) as Byte == b1)) return false;
	if (xx != b1 as ULong) return false;

	val yy = 0xFFFFffffFFFFffffUl;
	val i2 = -1n;
	val l2 = -1l;
	val b2 = -1y;
	if (!(yy as Long == l2)) return false;
	if (yy != l2 as ULong) return false;
	if (!((yy as Long) as Int == i2)) return false;
	if (yy != l2 as ULong) return false;
	if (!((yy as Long) as Byte == b2)) return false;
	if (yy != b2 as ULong) return false;

	return true;
    }

    public static def main(Rail[String]) {
        new ULongCast0().execute();
    }
}
