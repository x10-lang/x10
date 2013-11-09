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
 * Test overriding of methods with UShort parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UShortOverride1 extends x10Test {

    static class A[T] {
	def f(x:T) : String { return "A:T"; }
    }

    static class B extends A[UShort] {}

    static class C extends A[UShort] {
	def f(x:UShort) : String { return "C:UShort"; }
	def f(x:Short) : String { return "C:Short"; }
    }

    static class D extends C {}

    static class E extends D {
	def f(x:UShort) : String { return "E:UShort"; }
	def f(x:Short) : String { return "E:Short"; }
    }

    public def run() : Boolean {
	val a = new A[UShort]();
	val b = new B();
	val c = new C();
	val d = new D();
	val e = new E();
	assert a.f(1us).equals("A:T");
	assert b.f(1us).equals("A:T");
	assert c.f(1us).equals("C:UShort");
	assert c.f(1s).equals("C:Short");
	assert d.f(1us).equals("C:UShort");
	assert d.f(1s).equals("C:Short");
	assert e.f(1us).equals("E:UShort");
	assert e.f(1s).equals("E:Short");
	return true;
    }

    public static def main(Rail[String]) {
        new UShortOverride1().execute();
    }
}
