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
 * Test equality of UShorts.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UShortOverride0 extends x10Test {
    static class A[T] {
	def f(x:T) : String {
	    return "T";
	}
    }

    static class A0 extends A[UShort] {
    }

    static class AShort extends A[Short] {
	def f(x:UShort) : String { return "UShort"; }
    }

    static class AUShort extends A[UShort] {
	def f(x:Short) : String { return "Short"; }
    }

    static class B extends A[UShort] {
	def f(x:UShort) : String { return "UShort"; }
	def f(x:Short) : String { return "Short"; }
    }

    public def run() : Boolean {
	val a = new A[UShort]();
	val a0 = new A0();
	val ashort = new AShort();
	val aushort = new AUShort();
	val b = new B();

	var r : Boolean = true;
	r = r && "T".equals(a.f(1us));
	r = r && "T".equals(a0.f(1us));
	r = r && "T".equals(ashort.f(1s));
	r = r && "UShort".equals(ashort.f(1us));
	r = r && "T".equals(aushort.f(1us));
	r = r && "Short".equals(aushort.f(1s));
	r = r && "UShort".equals(b.f(1us));
	r = r && "Short".equals(b.f(1s));

	return r;
    }

    public static def main(Rail[String]) {
        new UShortOverride0().execute();
    }
}
