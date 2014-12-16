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
public class UShortOverride2 extends x10Test {

    static class A[T] {
	def f(x:T) : String { return "A:T"; }
    }

    static class F extends A[Short] {}

    static class G extends F {
	def f(x:UShort) : String { return "G:UShort"; }
	def f(x:Short) : String { return "G:Short"; }
    }

    public def run() : Boolean {
	val a = new A[Short]();
	val f = new F();
	val g = new G();
	assert a.f(1s).equals("A:T");
	assert f.f(1s).equals("A:T");
	assert g.f(1s).equals("G:Short");
	assert g.f(1us).equals("G:UShort");
	return true;
    }

    public static def main(Rail[String]) {
        new UShortOverride2().execute();
    }
}
