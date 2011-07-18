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

/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that field accesses within a depclause are checked to be final.
 *
 * @author vj
 */
public class NonFinalField_MustFailCompile extends x10Test {
   public var bad:boolean=true;
	class Test(i: int) {
	
	   public def this(ii:int):Test = {
	     property(ii);
	   }
	}
	
	public def run(): boolean = {
	   var a: Test =  new Test(52) as
	    Test{i==52, bad}; // ERR
	    return true;
	}
	public static def main(var args: Array[String](1)): void = {
		new NonFinalField_MustFailCompile().execute();
	}
}
