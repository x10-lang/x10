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
 * Test overriding of methods with ULong parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class ULongOverride1 extends x10Test {

    static class A[T] {
	def f(x:T) : String { return "A:T"; }
    }

    static class B extends A[ULong] {}

    static class C extends A[ULong] {
	def f(x:ULong) : String { return "C:ULong"; }
	def f(x:Long) : String { return "C:Long"; }
    }

    static class D extends C {}

    static class E extends D {
	def f(x:ULong) : String { return "E:ULong"; }
	def f(x:Long) : String { return "E:Long"; }
    }

    public def run() : Boolean {
	val a = new A[ULong]();
	val b = new B();
	val c = new C();
	val d = new D();
	val e = new E();
	assert a.f(1ul).equals("A:T");
	assert b.f(1ul).equals("A:T");
	assert c.f(1ul).equals("C:ULong");
	assert c.f(1l).equals("C:Long");
	assert d.f(1ul).equals("C:ULong");
	assert d.f(1l).equals("C:Long");
	assert e.f(1ul).equals("E:ULong");
	assert e.f(1l).equals("E:Long");
	return true;
    }

    public static def main(Rail[String]) {
        new ULongOverride1().execute();
    }
}
