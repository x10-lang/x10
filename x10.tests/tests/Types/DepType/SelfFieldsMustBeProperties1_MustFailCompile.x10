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
 * The test checks that only properties can be accessed via self.
 *
 * @author vj
 */
public class SelfFieldsMustBeProperties1_MustFailCompile extends x10Test {

class Test(i:int) {
	   public val bad:boolean; // not declared as a property.
	   public def this(ii:int):Test {
	     property(ii);
	     bad = true;
	   }
	}
	
	
	public def run(): boolean {
	   var a: Test{i==52n} = new Test(52n) as Test{i==52n && self.bad};  // ERR: Only properties may be prefixed with self in a constraint.
	    return true;
	}
	public static def main(var args: Rail[String]): void {
		new SelfFieldsMustBeProperties1_MustFailCompile().execute();
	}
}
