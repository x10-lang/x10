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

/** Tests that a method of a class C, guarded with this(:c), is accessed only in objects
 * whose type is a subtype of C(:c).
 *@author pvarma
 *
 */

import harness.x10Test;

public class GuardedMethodAccess extends x10Test { 

   class Test(i:int, j:int) {
		public var v: int = 0n;
		def this(i:int, j:int):Test{self.i==i,self.j==j} {
			property(i,j);
		}
		public def  key(){i==j}=5n;
	}
	
		
	public def run(): boolean {
		var t: Test{i==j} = new Test(5n, 5n);
		t.v = t.key() + 1n;
	   return true;
	}  
	
    public static def main(var args: Rail[String]): void {
        new GuardedMethodAccess().execute();
    }
   

		
}
