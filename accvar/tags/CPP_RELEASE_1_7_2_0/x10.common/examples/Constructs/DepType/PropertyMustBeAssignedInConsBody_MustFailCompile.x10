/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//The current release does not implement the check that for every constructor
// with a defined return type, every path to the exit node contains 
// an invocation of property that is strong enough to entail the return type.


/** Test that the compiler detects a situation in which one branch of a conditional has
    a property clause but not another.
 *@author vj
 *
 */

import harness.x10Test;

public class PropertyMustBeAssignedInConsBody_MustFailCompile extends x10Test { 

    class Tester(int(:self == 2) i ) {
      public Tester(final int arg) { 
      } 
    }
	
 
    public boolean run() { 
	 return true;
    }
	
    public static void main(String[] args) {
        new PropertyMustBeAssignedInConsBody_MustFailCompile().execute();
    }
   

		
}