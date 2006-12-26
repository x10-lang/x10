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

	class Test(int i, int j) {
		Test (: self.i == self.j) (final int i, final int(:self==i) j) {
		    property(i,j);
		}
	}
		
	class Test2(int k) extends Test{
		Test2(final int k) {
			super(k,k);
			property(k);
		}
	}
	public boolean run() {
	   return true;
	}  
	
    public static void main(String[] args) {
        new ConstructorInvocation2().execute();
    }
   

		
}
