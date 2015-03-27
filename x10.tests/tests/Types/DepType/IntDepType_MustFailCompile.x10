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

/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a depclause can be added to primitive types such as int
 * and that self==k clauses are checked.
 *
 * @author vj
 */
public class IntDepType_MustFailCompile extends x10Test {
    class Test(i:int, j:int) {
       public def this(i:int, j:int):Test { property(i, j); }
    }
  
	public def run(): boolean {
		var i: int{self == 0n} = 3n;  // ERR
	   return true;
	}
	public static def main(Rail[String]) {
		new IntDepType_MustFailCompile().execute();
	}
}
