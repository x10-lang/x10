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
public class Variance3 extends x10Test {
        interface Set[T] { def set(y: T): void; }
        class SetImpl[T] implements Set[T] {
                        var x: T;
                        def this(y: T) = { x = y; }
                        public def set(y: T): void = { x = y; }  }

        class A { }
        class B extends A { }

        public def run(): boolean = {
                val a = new A();
                val b = new B();

                val sa = new SetImpl[A](a);
                val sb = new SetImpl[B](b);

                sa.set(b);
                sb.set(b);

                val sx: Set[A] = sa;
                sx.set(b);

                val sy: Set[B] = sb;
                sy.set(b);

                return true;
        }

	public static def main(var args: Rail[String]): void = {
		new Variance3().execute();
	}
}

