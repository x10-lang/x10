/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//The current release does not implement guarded methods or fields.

/** Tests that a method of a class C, guarded with this(:c), is accessed only in objects
 * whose type is a subtype of C(:c).
 *@author pvarma
 *
 */

import harness.x10Test;

public class GuardedMethodAccess_MustFailCompile extends x10Test { 

  class Test(i:int, j:int) {
		public var v: int = 5;
		def this(i:int, j:int):Test{self.i==i,self.j==j} = {
			property(i,j);
		}
		public def  key(){i==j}=5;
	}
	
		
	public def run(): boolean = {
		var t: Test = new Test(5, 6);
		t.v = t.key() + 1; // should fail typecheck. 
	   return true;
	}  
	
    public static def main(var args: Rail[String]): void = {
        new GuardedMethodAccess_MustFailCompile().execute();
    }
   

		
}
