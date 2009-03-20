/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//The current release does not implement guarded methods or fields.

/**Tests that a field of a class C, guarded with this(:c), is accessed only in objects
 * whose type is a subtype of C(:c).
 *@author pvarma
 *
 */

import harness.x10Test;

public class GuardedFieldAccess_MustFailCompile extends x10Test { 

	class Test(int i, int j) {
		public this(:i==j) int value = 5;
		Test (final int i, final int j ) {
			property(i,j);
		}
	}
		
	public boolean run() {
		Test t = new Test(6, 5);
		t.value = t.value + 1; // Must fail. t needs to be of type Test(:i==j).
	   return true;
	}  
	
    public static void main(String[] args) {
        new GuardedFieldAccess_MustFailCompile().execute();
    }
   

		
}
