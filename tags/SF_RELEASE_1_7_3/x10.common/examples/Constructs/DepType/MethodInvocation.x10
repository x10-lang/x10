/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests that a method invocation satisfies the parameter types and constraint
 * of the method declaration.
 *@author pvarma
 *
 */

import harness.x10Test;

public class MethodInvocation extends x10Test { 

	class Test(int i, int j) {
		public int tester (final int k, final int(:self==k) l /*: k == l*/) {
			return k + l;}
		Test (int i, int j ) {
			property(i,j)
		}
	}
		

	public boolean run() {
		Test t = new Test (1, 2);
		// the following call types correctly
		t.tester(3, 3);
	   return true;
	}  
	
    public static void main(String[] args) {
        new MethodInvocation().execute();
    }
   

		
}
