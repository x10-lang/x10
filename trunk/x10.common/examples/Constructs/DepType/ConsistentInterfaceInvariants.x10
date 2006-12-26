/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
//Interface invariants are not currently implemented.
/** Tests that invariants due to a super constraint and a sub constraint are 
 * consistent with each other.
 *@author pvarma
 *
 */

import harness.x10Test;

public class ConsistentInterfaceInvariants extends x10Test { 

    public static interface Test (int l, int m : m == l ) {
     public int put();
    }
    
    public static interface Test1 (int n: l == n && m == n) extends Test { 
     public int foo();
    }
    
    public boolean run() { 
	 return true;
    }
	
    public static void main(String[] args) {
        new ConsistentInterfaceInvariants().execute();
    }
   

		
}
