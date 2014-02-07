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
 * Check that a class with a property P p cannot have an ancestor 
 * with a property named p (no property name overriding) 
 *
 * @author pvarma
 */
public class SamePropertyAncestor_MustFailCompile extends x10Test {
	class Test(i: int, j:int) {
		 
		def this(i:int, j:int):Test = {
			property(i,j);
		}
	}
	
	class Test2(i: int) extends Test {  // ERR ([Semantic Error: Class cannot override property of superclass.])
		 
		def this(i:int):Test2 = {
		    super(i,i);
			property(i);
		}
	}
		
	
	public def run(): boolean = {
		val a = new Test2(1n);
	   return true;
	}
	public static def main(var args: Rail[String]): void = {
		new SamePropertyAncestor_MustFailCompile().execute();
	}
}
