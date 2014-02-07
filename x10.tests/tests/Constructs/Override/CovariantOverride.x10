/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

/**
 * Test compilation of methods with covariant override.
 */
public class CovariantOverride extends x10Test {

    static class A[T] {
	def f() : Any = 1;
	def g() : Any = "abc";
	def h() : T = 1 as T; // ERR: Warning: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
    }

    static class B extends A[UInt] {
	def f() : Int = 2n;
	def g() : String = "efg";
	def h() : UInt = 3un;
    }

    public def run(): boolean = {
	val b = new B();
	chk(b.f() == 2n);
	chk(b.g().equals("efg"));
	chk(b.h() == 3un);
	return true;
    }

    public static def main(Rail[String]) = {
        new CovariantOverride().execute();
    }
}
