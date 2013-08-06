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
public class UIntOverride2 extends x10Test {

    static class A[T] {
	def f(x:T) : String { return "A:T"; }
    }

    static class F extends A[Int] {}

    static class G extends F {
	def f(x:UInt) : String { return "G:UInt"; }
	def f(x:Int) : String { return "G:Int"; }
    }

    public def run() : Boolean {
	val a = new A[Int]();
	val f = new F();
	val g = new G();
	assert a.f(1n).equals("A:T");
	assert f.f(1n).equals("A:T");
	assert g.f(1n).equals("G:Int");
	assert g.f(1un).equals("G:UInt");
	return true;
    }

    public static def main(Rail[String]) {
        new UIntOverride2().execute();
    }
}
