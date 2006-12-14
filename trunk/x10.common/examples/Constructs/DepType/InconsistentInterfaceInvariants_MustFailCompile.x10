/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests that invariants due to a super constraint and a sub constraint are 
 * consistent with each other.
 *@author pvarma
 *
 */

import harness.x10Test;

public class InconsistentInterfaceInvariants_MustFailCompile extends x10Test { 

    public static interface Test (int l, int m : m == l ) {
     public int put();
    }
    
    public static interface Test1 (: l == 0 && m == 1) extends Test { 
     public int foo();
    }
    
    public boolean run() { 
	 return true;
    }
	
    public static void main(String[] args) {
        new InconsistentInterfaceInvariants_MustFailCompile().execute();
    }
   

		
}
