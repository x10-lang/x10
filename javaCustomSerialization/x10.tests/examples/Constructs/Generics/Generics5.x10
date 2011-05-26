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
 * Test a generics class with an invariant parameter.
 *
 * @author nystrom 8/2008
 */
public class Generics5 extends x10Test {
        public def run(): boolean = {
                var result: boolean = true;

                val v = new Rail[int](3, (i:int) => 2*i);
                for (var i: int = 0; i < v.size; i++)
                        result &= v(i) == (i*2);

                return result;
        }

	public static def main(var args: Array[String](1)): void = {
		new Generics5().execute();
	}
}

