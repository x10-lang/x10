/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

/**
 * Region algebra.
 *
 * @author nystrom 8/2008
 */
public class Subclassing2 extends x10Test {
        class Get[T] {
                val x: T;
                def this(y: T) = { x = y; }
                def get(): T = x; }
        class GetB extends Get[B] {
                def this(y: B) = { super(y); } }

        class A { }
        class B extends A { }

        public def run(): boolean = {
                new Get[A](new A());
                new Get[B](new B());
                new GetB(new B());
                return true;
        }

	public static def main(var args: Rail[String]): void = {
		new Subclassing2().execute();
	}
}
