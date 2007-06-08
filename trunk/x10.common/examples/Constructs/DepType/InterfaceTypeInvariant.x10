/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests that the properties of an interface are implemented by a compliant class 
 * and that the interface constraint is entailed by the compliant class.
 *@author pvarma
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the release.
// Invariants in interfaces are not curently supported.

import harness.x10Test;

public class InterfaceTypeInvariant extends x10Test { 

    public static interface Test (int l, int m : m == l ) {
     public int put();
    }
    
    class Tester(int l, int m : m == l ) implements Test {
      public Tester(int arg) { property(arg,arg); }
      public int put() {
        return 0;
      }
	}
 
    public  boolean run() { 
	 return true;
    }
	
    public static void main(String[] args) {
        new InterfaceTypeInvariant().execute();
    }
   

		
}
