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
 * Test conversion of UInt to String.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UIntToString2 extends x10Test {

    static def toString(x:Any):String {
	return x.toString();
    }

    static class A[T] {
	def toString(x:T):String {
	    return x.toString();
	}
    }

    public def run(): boolean {
	val u = 0xFFFFffffUN;
	val s1 = u.toString();
	val s2 = toString(u);
	val s3 = new A[UInt]().toString(u);
	return (s1.equals("4294967295") && s2.equals("4294967295") && s3.equals("4294967295"));
    }

    public static def main(Rail[String]) {
        new UIntToString2().execute();
    }
}
