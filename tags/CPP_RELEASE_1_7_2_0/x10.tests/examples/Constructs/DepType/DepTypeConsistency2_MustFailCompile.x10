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

public class DepTypeConsistency2_MustFailCompile extends x10Test { 

/* free variable i is not parametrically consistent */
    class Tester(i:int{self == 2,self==3}) {
      public def this(arg:int):Tester = { property(arg);}
    }
	
    public def run()=true; 
	
   public static def main(var args: Rail[String]): void = {
        new DepTypeConsistency2_MustFailCompile().execute();
    }
}
