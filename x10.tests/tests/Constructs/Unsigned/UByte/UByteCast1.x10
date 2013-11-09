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
 * Test casting of UByte to UInt
 *
 * @author Salikh Zakirov 5/2011
 */
public class UByteCast1 extends x10Test {
    public def run(): boolean = {
	val xx = 1uy;
	val u1 = 1un;
	val i1 = 1n;
	if (!(xx as UInt == u1)) return false;
	if (xx as UInt != u1) return false;
	if (!(xx == u1 as UByte)) return false;
	if (xx != u1 as UByte) return false;
	//if (!(xx as Int == i1)) return false; // fails compilation
	//if (xx as Int != i1) return false;	// fails compilation

	val yy = 0xFFuy;
	val i2 = 0xffn;
	val u2 = 0xFFun;
	if (!(yy as UInt == u2)) return false;
	if (yy as UInt != u2) return false;
	//if (!(yy as Int == i2)) return false; // fails compilation
	//if (yy as Int != i2) return false;    // fails compilation
	if (!(yy == -1y as UByte)) return false;
	if (yy != -1y as UByte) return false;

	return true;
    }

    public static def main(Rail[String]) {
        new UByteCast1().execute();
    }
}
