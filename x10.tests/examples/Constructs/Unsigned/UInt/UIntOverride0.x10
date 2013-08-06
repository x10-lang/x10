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
 * Test equality of UInts.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UIntOverride0 extends x10Test {
    static class A[T] {
	def f(x:T) : String {
	    return "T";
	}
    }

    static class A0 extends A[UInt] {
    }

    static class AInt extends A[Int] {
	def f(x:UInt) : String { return "UInt"; }
    }

    static class AUInt extends A[UInt] {
	def f(x:Int) : String { return "Int"; }
    }

    static class B extends A[UInt] {
	def f(x:UInt) : String { return "UInt"; }
	def f(x:Int) : String { return "Int"; }
    }

    public def run() : Boolean {
	val a = new A[UInt]();
	val a0 = new A0();
	val aint = new AInt();
	val auint = new AUInt();
	val b = new B();

	var r : Boolean = true;
	r = r && "T".equals(a.f(1un));
	r = r && "T".equals(a0.f(1un));
	r = r && "T".equals(aint.f(1n));
	r = r && "UInt".equals(aint.f(1un));
	r = r && "T".equals(auint.f(1un));
	r = r && "Int".equals(auint.f(1n));
	r = r && "UInt".equals(b.f(1un));
	r = r && "Int".equals(b.f(1n));

	return r;
    }

    public static def main(Rail[String]) {
        new UIntOverride0().execute();
    }
}
