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

	class Test(int i, int j) {
		Test (: i == j) (final int i, final int(:j==i) j ) {
			property(i,j);
		}
	}
		
	class Test2(int k) extends Test{
		Test2(int k) {
		// the call to super below violates the constructor parameters constraint i == j
			super(0,1);
			property(k);
		}
	}
	public boolean run() {
	   return true;
	}  
	
    public static void main(String[] args) {
        new ConstructorInvocation_MustFailCompile().execute();
    }
   

		
}
