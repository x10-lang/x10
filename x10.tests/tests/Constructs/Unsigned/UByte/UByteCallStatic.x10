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
 * Test calling with UByte parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UByteCallStatic extends x10Test {
    public static def f(a:UByte) {
	 return "ubyte = " + a;
    }

    public static def f(a:Byte) {
	return "byte = " + a;
    }

    public def run(): boolean {
	val i0 = 0y;
	val u1 = 1uy;
	val s0 = f(i0);
	val s1 = f(u1);
	return (s0.equals("byte = 0") && s1.equals("ubyte = 1"));
    }

    public static def main(Rail[String]) {
        new UByteCallStatic().execute();
    }
}
