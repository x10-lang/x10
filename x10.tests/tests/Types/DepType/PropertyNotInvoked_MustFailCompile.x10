/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

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

   class Tester(i: int(2n) ) {
      public def this(arg:int):Tester{self.i==2n} { // ERR: property(...) might not have been called
      
         if (arg==2n)
          property(arg as int{self==2n});
	     else {
	      this.i=2n; // ERR: Must use property(...) to assign to a property.
	     }
      }
    }
   
    public def run()=true;
	
    public static def main(Rail[String]) {
        new PropertyNotInvoked_MustFailCompile().execute();
    }
}
