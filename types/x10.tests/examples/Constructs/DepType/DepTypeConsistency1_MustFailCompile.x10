/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

/** Tests that a free variable in DepType that is not parametrically consistent 
 * causes compilation failure.
 *@author pvarma
 *
 */

import harness.x10Test;

public class DepTypeConsistency1_MustFailCompile extends x10Test { 

/* free variable i is not parametrically consistent */
    class Tester(i:int{self == 2}){i == 3} {  // ShouldBeErr
      public def this(arg:int):Tester = { property(arg);}  // ShouldNotBeERR ShouldNotBeERR (The return type of the constructor (DepTypeConsistency1_MustFailCompile.Tester) must be derived from the type of the class (DepTypeConsistency1_MustFailCompile.Tester) on which the constructor is defined.)
    }
	
    public def run()=true; 
	
   public static def main(var args: Array[String](1)): void = {
        new DepTypeConsistency1_MustFailCompile().execute();
    }
}
