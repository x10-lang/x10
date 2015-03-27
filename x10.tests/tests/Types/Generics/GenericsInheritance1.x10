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
 * Test inheritance and generics.
 * This tests a code generation bug.  The generated code should NOT be this:
 *
 *     class I<T> { T m(); }
 *     class C { public int m() { return 0; } }
 *     class D extends C implements I<Integer>
 *     {
 *     }
 *
 * The problem is that, to implement I<Integer>, D needs to have:
 *
 *     public Integer m() { return m(); }
 *
 * But that would have a name conflict with int m().
 *
 * We should instead generate:
 *
 *     class I<T> { T m$(); }
 *     class C { public int m() { return 0; } }
 *     class D extends C implements I<Integer>
 *     {
 *         public Integer m$() { return this.m(); }
 *     }
 *
 * @author nystrom 8/2008
 */
public class GenericsInheritance1 extends x10Test {
        interface I[T] { def m(): T; }
        class C { public def m():long { return 0; } }
        class D extends C implements I[long] { }

	public def run(): boolean {
		return true;
	}

	public static def main(var args: Rail[String]): void {
		new GenericsInheritance1().execute();
	}
}

