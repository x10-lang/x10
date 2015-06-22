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
 * Region algebra.
 *
 * @author nystrom 8/2008
 */
public class Variance2 extends x10Test {
        class Get[T] {
                val x: T;
                def this(y: T) { x = y; }
                def get(): T = x; }

        class A { }
        class B extends A { }

        public def run(): boolean {
                val ga = new Get[A](new A());
                val a = ga.get();

                val gx : Get[A] = ga;
                val x = gx.get();

                return gx == ga && x == a;
        }

	public static def main(var args: Rail[String]): void {
		new Variance2().execute();
	}
}
