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

/** Tests that a free variable in DepType that is not parametrically consistent 
 * causes compilation failure.
 *@author pvarma
 *
 */

import harness.x10Test;

public class DepTypeConsistency1_MustFailCompile extends x10Test { 

/* free variable i is not parametrically consistent */
    class Tester(i:int{self == 2n}){i == 3n} {  // ERR ERR ERR : [Semantic Error: Invalid type; the real clause of DepTypeConsistency1_MustFailCompile.Tester{self==DepTypeConsistency1_MustFailCompile.Tester#this} is inconsistent. , Semantic Error: Invalid type; the real clause of DepTypeConsistency1_MustFailCompile.Tester is inconsistent. , Semantic Error: Class invariant is inconsistent.]
      public def this(arg:int):Tester { property(arg);}  // ERR ShouldNotBeERR ShouldNotBeERR: [Semantic Error: Invalid type; the real clause of DepTypeConsistency1_MustFailCompile.Tester{self.i==arg} is inconsistent.]
    }
	
    public def run()=true; 
	
   public static def main(var args: Rail[String]): void {
        new DepTypeConsistency1_MustFailCompile().execute();
    }
}
