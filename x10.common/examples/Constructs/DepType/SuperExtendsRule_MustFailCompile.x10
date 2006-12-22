/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests that the constraint d of an extended type D(:d) is entailed by the 
 * type returned by the constructor of the subtype.
 *@author pvarma
 *
 */

import harness.x10Test;

public class SuperExtendsRule_MustFailCompile extends x10Test { 

	class Test(int i, int j) {
		Test(:self.i==i&&self.j==j)(final int i, final int j) {
			property(i,j);
		}
	}
		
	class Test2(int k) extends Test {
		Test2(:i==j)(final int k) {
		// the call to super below violates the constraint i == j
			super(0,1);
			property(k);
		}
	}
	public boolean run() {
	   return true;
	}  
	
    public static void main(String[] args) {
        new SuperExtendsRule_MustFailCompile().execute();
    }
   

		
}
