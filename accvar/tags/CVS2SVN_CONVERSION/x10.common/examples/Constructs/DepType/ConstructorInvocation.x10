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
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
// depclauses in parameter lists of methods and constructors are not currently implemented.
// Work-around: add the depclause to the deptypes of the formal arguments.

import harness.x10Test;

public class ConstructorInvocation extends x10Test { 

	class Test(int i, int j) {
		Test (: i == j) (final int i, final int j : i == j) {
		    property(i,j);
		}
	}
		
	class Test2(int k) extends Test{
		Test2(int k) {
			super(k,k);
			property(k);
		}
	}
	public boolean run() {
	   return true;
	}  
	
    public static void main(String[] args) {
        new ConstructorInvocation().execute();
    }
   

		
}
