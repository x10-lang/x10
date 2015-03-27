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
 * Test type parameter bounds.
 *
 * @author nystrom 8/2008
 */
public class Bounds4 extends x10Test {
        class A { }
        class B extends A { }

        class C[T]{T<:A} {
                var x: T;
                def this(y: T) { x = y; }
        }

	public def run(): boolean {
                val a = new A();
                return new C[A](a).x == a;
	}

	public static def main(var args: Rail[String]): void {
		new Bounds4().execute();
	}
}

