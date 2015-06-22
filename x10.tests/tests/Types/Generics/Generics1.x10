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
 * Test a generics class with an invariant parameter.
 *
 * @author nystrom 8/2008
 */
public class Generics1 extends x10Test {
        class Get[T] {
                val x: T;
                def this(y: T) { x = y; }
                def get(): T = x; }

	public def run(): boolean {
                val c: Generics1 = new Get[Generics1](this).get();
                x10.io.Console.OUT.println("c = " + c);
                x10.io.Console.OUT.println("this = " + this);
		return c == this;
	}

	public static def main(var args: Rail[String]): void {
		new Generics1().execute();
	}
}

