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
// Cannot constrain super-properties in the class invariant
/** Tests that invariants due to a super constraint and a sub constraint are 
 * consistent with each other.
 *@author pvarma
 *
 */

import harness.x10Test;

public class ConsistentInterfaceInvariants extends x10Test { 

    public static interface Test (l:int, m:int){m == l} {
	public def put():int;
    }
    
    public static interface Test1 (n:int){l == n && m == n} extends Test { 
	public def foo():int;
    }
    
    public def run():boolean=true;
	
    public static def main(a: Rail[String]) = {
        new ConsistentInterfaceInvariants().execute();
    }
   

		
}
