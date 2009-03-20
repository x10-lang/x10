/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests that two guarded fields may not have the same name, even if their guard
 * conditions are disjoint.
 *@author pvarma
 *
 */

import harness.x10Test;

public class SameNameGuardedFieldAccess_MustFailCompile extends x10Test { 

	class Test(int i, int j) {
		 this(:i==5) public int value = 5;
		this(:i==6) public int value = 6;
		Test (final int i, final int j ) {
			this.i=i;
			this.j=j;
		}
	}
		
	public boolean run() {
		Test(: i==5) t = (Test (: i == 5)) new Test(5, 5);
		t.value = t.value + 10;
	   return true;
	}  
	
    public static void main(String[] args) {
        new SameNameGuardedFieldAccess_MustFailCompile().execute();
    }
   

		
}
