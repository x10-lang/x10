/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Test inheritance and generics.
 *
 * @author nystrom 8/2008
 */
public class GenericsInheritance2_MustFailCompile extends x10Test {
        interface I[T] { def m(): T; }
        class C implements I[int], I[float] {
                /* conflict, also can't implement m anyway */
        }

	public def run(): boolean = {
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new GenericsInheritance2_MustFailCompile().execute();
	}
}

