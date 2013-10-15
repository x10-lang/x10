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
 * Test overriding of methods with UByte parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UByteOverride1 extends x10Test {

    static class A[T] {
	def f(x:T) : String { return "A:T"; }
    }

    static class B extends A[UByte] {}

    static class C extends A[UByte] {
	def f(x:UByte) : String { return "C:UByte"; }
	def f(x:Byte) : String { return "C:Byte"; }
    }

    static class D extends C {}

    static class E extends D {
	def f(x:UByte) : String { return "E:UByte"; }
	def f(x:Byte) : String { return "E:Byte"; }
    }

    public def run() : Boolean {
	val a = new A[UByte]();
	val b = new B();
	val c = new C();
	val d = new D();
	val e = new E();
	assert a.f(1uy).equals("A:T");
	assert b.f(1uy).equals("A:T");
	assert c.f(1uy).equals("C:UByte");
	assert c.f(1y).equals("C:Byte");
	assert d.f(1uy).equals("C:UByte");
	assert d.f(1y).equals("C:Byte");
	assert e.f(1uy).equals("E:UByte");
	assert e.f(1y).equals("E:Byte");
	return true;
    }

    public static def main(Rail[String]) {
        new UByteOverride1().execute();
    }
}
