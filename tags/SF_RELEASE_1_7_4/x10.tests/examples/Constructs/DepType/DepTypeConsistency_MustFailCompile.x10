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

public class DepTypeConsistency_MustFailCompile extends x10Test { 
   val j:int=3;
/* free variable j is not parametrically consistent */
    class Tester(i:int){j == 2} {
      public def this(arg:int):Tester { property(arg);}
    }
	
     public def run()=true; 
	
   public static def main(var args: Rail[String]): void = {
        new DepTypeConsistency_MustFailCompile().execute();
    }
}
