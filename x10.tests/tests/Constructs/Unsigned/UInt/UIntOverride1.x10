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
 * Test overriding of methods with UInt parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UIntOverride1 extends x10Test {

    static class A[T] {
	def f(x:T) : String { return "A:T"; }
    }

    static class B extends A[UInt] {}

    static class C extends A[UInt] {
	def f(x:UInt) : String { return "C:UInt"; }
	def f(x:Int) : String { return "C:Int"; }
    }

    static class D extends C {}

    static class E extends D {
	def f(x:UInt) : String { return "E:UInt"; }
	def f(x:Int) : String { return "E:Int"; }
    }

    public def run() : Boolean {
	val a = new A[UInt]();
	val b = new B();
	val c = new C();
	val d = new D();
	val e = new E();
	assert a.f(1un).equals("A:T");
	assert b.f(1un).equals("A:T");
	assert c.f(1un).equals("C:UInt");
	assert c.f(1n).equals("C:Int");
	assert d.f(1un).equals("C:UInt");
	assert d.f(1n).equals("C:Int");
	assert e.f(1un).equals("E:UInt");
	assert e.f(1n).equals("E:Int");
	return true;
    }

    public static def main(Rail[String]) {
        new UIntOverride1().execute();
    }
}
