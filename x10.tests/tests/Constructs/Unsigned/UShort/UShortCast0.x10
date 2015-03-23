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
 * Test casting of Short to UShort
 *
 * @author Salikh Zakirov 5/2011
 */
public class UShortCast0 extends x10Test {
    public def run(): boolean {
	val xx = 1us;
	val i1 = 1n;
	val s1 = 1s;
	val b1 = 1y;
	if (!(xx as Short == s1)) return false;
	if (xx != i1 as UShort) return false;
	if (!((xx as Short) as Int == i1)) return false;
	if (xx != s1 as UShort) return false;
	if (!((xx as Short) as Byte == b1)) return false;
	if (xx != b1 as UShort) return false;

	val yy = 0xFFFFus;
	val i2 = -1n;
	val s2 = -1s;
	val b2 = -1y;
	if (!(yy as Short == s2)) return false;
	if (yy != s2 as UShort) return false;
	if (!((yy as Short) as Int == i2)) return false;
	if (yy != s2 as UShort) return false;
	if (!((yy as Short) as Byte == b2)) return false;
	if (yy != b2 as UShort) return false;

	return true;
    }

    public static def main(Rail[String]) {
        new UShortCast0().execute();
    }
}
