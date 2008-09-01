/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests that a constructor invocation satisfies the parameter types and constraint
 * of the constructor declaration.
 *@author pvarma
 *
 */

import harness.x10Test;

public class ConstructorInvocation_MustFailCompile extends x10Test { 

	class Test(i:int, j:int) {
	    def this(i:int, j:int{self==i}):Test{self.i==self.j} = {
		property(i,j);
	    }
	}
		
	class Test2(k:int) extends Test {
	    def this(k:int):Test2 = { 
		// the call to super below violates the constructor parameters constraint i == j
		super(0,1);
		property(k);
	    }
	}
	public def run()=true;
	
    public static def main(a: Rail[String]) = {
        new ConstructorInvocation_MustFailCompile().execute();
    }
   

		
}
