/**
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

public class PropertyNotInvoked_MustFailCompile extends x10Test { 

   class Tester(i: int(2) ) {
      public def this(arg:int):Tester{i=arg} { 
         i=2;
          property((int(:self==2)) arg);
          }
	  else {
	      i=2;
	  }*/
      } 
    }
   
 
    public def run(): boolean = { 
	 return true;
    }
	
    public static def main(var args: Rail[String]): void = {
        new PropertyNotInvoked_MustFailCompile().execute();
    }
   

		
}
