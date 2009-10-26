/**
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

import harness.x10Test;

public class InterfaceTypeInvariant_MustFailCompile extends x10Test { 

    public static interface Test (n:int, m: int{m==n}) {
       def put():int;
    }
    
    class Tester(l: int, m:int){m == 2 && l == 3} implements Test{
      public def this():Tester = { property(3,2); }
      public def put()=0;
	}
 
    public def run()=true;
   
    public static def main(Rail[String]): void = {
        new InterfaceTypeInvariant_MustFailCompile().execute();
    }
   

		
}
