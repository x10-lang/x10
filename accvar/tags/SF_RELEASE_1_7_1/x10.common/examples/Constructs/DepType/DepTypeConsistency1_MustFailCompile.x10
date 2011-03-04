/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests that a free variable in DepType that is not parametrically consistent 
 * causes compilation failure.
 *@author pvarma
 *
 */

import harness.x10Test;

public class DepTypeConsistency1_MustFailCompile extends x10Test { 

/* free variable i is not parametrically consistent */
    class Tester(int(:i == 2) i : i == 3 ) {
      public Tester(int arg) { property(arg);}
    }
	
 
    public boolean run() { 
	 return true;
    }
	
    public static void main(String[] args) {
        new DepTypeConsistency1_MustFailCompile().execute();
    }
   

		
}
