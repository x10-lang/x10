/*
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

	class Test(int i, int j) {
		public int value = 0;
		 this(:i==j) public int key() {return 5;}
		Test(:self.i==i&&self.j==j) (final int i, final int j ) {
			property(i,j);
		}
	}
		
	public boolean run() {
		Test(: i==j) t =  new Test(5, 5);
		t.value = t.key() + 1;
	   return true;
	}  
	
    public static void main(String[] args) {
        new GuardedMethodAccess().execute();
    }
   

		
}
