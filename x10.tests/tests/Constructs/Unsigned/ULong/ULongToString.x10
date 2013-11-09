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
 * Test conversion of ULong to String.
 *
 * @author Salikh Zakirov 5/2011
 */
public class ULongToString extends x10Test {

    static def toString(x:Any):String {
	return x.toString();
    }

    static class A[T] {
	def toString(x:T):String {
	    return x.toString();
	}
    }

    public def run(): boolean = {
	val u = 1ul;
	val s1 = u.toString();
	val s2 = toString(u);
	val s3 = new A[ULong]().toString(u);
	return (s1.equals("1") && s2.equals("1") && s3.equals("1"));
    }

    public static def main(Rail[String]) {
        new ULongToString().execute();
    }
}
