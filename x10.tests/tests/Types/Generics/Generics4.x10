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
public class Generics4 extends x10Test {
        class Gen[T] {
                var y: T;
                
                public def this(x: T) = { y = x; }
                
                public def set(x: T): void = {
                  y = x;
                }
                
                public def get(): T = y;
        }

        public def run(): boolean = {
                var result: boolean = true;

                val s = new Gen[String]("hi");
                val x = s.get();

                s.set("hello");
                val y = s.get();

                result &= x.equals("hi");
                result &= y.equals("hello");

                return result;
        }

	public static def main(var args: Rail[String]): void = {
		new Generics4().execute();
	}
}

