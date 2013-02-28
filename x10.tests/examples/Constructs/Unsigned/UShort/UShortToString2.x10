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
 * Test conversion of UShort to String.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UShortToString2 extends x10Test {

    static def toString(x:Any):String {
	return x.toString();
    }

    static class A[T] {
	def toString(x:T):String {
	    return x.toString();
	}
    }

    public def run(): boolean = {
	var r : Boolean = true;
	val u = 0xFFFFus;
	val s1 = u.toString();
	if (!s1.equals("65535")) { p("0xFFFFus.toString() = " + s1); r = false; }
	val s2 = toString(u);
	if (!s2.equals("65535")) { p("(... as Any).toString() = " + s2); r = false; }
	val s3 = new A[UShort]().toString(u);
	if (!s3.equals("65535")) { p("(... as T).toString() = " + s3); r = false; }
	return r;
    }

    public static def main(Rail[String]) {
        new UShortToString2().execute();
    }

    public static def p(msg:Any) {
	Console.OUT.println(msg);
    }
}
