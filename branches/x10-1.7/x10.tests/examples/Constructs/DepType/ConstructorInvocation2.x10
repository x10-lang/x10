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

public class ConstructorInvocation2 extends x10Test { 

	class Test(i:int, j:int) {
	    def this(i:int, j:int{self==i}):Test{self.i==self.j} = {
		property(i,j);
	    }
	}
		
	class Test2(k:int) extends Test{
	    def this(k:int):Test2 = {
		super(k,k);
		property(k);
	    }
	}
    public def run()=true;
	
    public static def main(var args: Rail[String]): void = {
        new ConstructorInvocation2().execute();
    }
   

		
}
