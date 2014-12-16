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
 * Test equality of ULongs.
 *
 * @author Salikh Zakirov 5/2011
 */
public class ULongOverride0 extends x10Test {
    static class A[T] {
	def f(x:T) : String {
	    return "T";
	}
    }

    static class A0 extends A[ULong] {
    }

    static class ALong extends A[Long] {
	def f(x:ULong) : String { return "ULong"; }
    }

    static class AULong extends A[ULong] {
	def f(x:Long) : String { return "Long"; }
    }

    static class B extends A[ULong] {
	def f(x:ULong) : String { return "ULong"; }
	def f(x:Long) : String { return "Long"; }
    }

    public def run() : Boolean {
	val a = new A[ULong]();
	val a0 = new A0();
	val along = new ALong();
	val aulong = new AULong();
	val b = new B();

	var r : Boolean = true;
	r = r && "T".equals(a.f(1ul));
	r = r && "T".equals(a0.f(1ul));
	r = r && "T".equals(along.f(1l));
	r = r && "ULong".equals(along.f(1ul));
	r = r && "T".equals(aulong.f(1ul));
	r = r && "Long".equals(aulong.f(1l));
	r = r && "ULong".equals(b.f(1ul));
	r = r && "Long".equals(b.f(1l));

	return r;
    }

    public static def main(Rail[String]) {
        new ULongOverride0().execute();
    }
}
