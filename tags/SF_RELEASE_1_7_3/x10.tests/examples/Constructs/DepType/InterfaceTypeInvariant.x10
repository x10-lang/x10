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


import harness.x10Test;

public class InterfaceTypeInvariant extends x10Test { 

    public static interface Test (l:int, m:int){this.m == this.l} {
      def put():int;
    }
    
    class Tester(l:int, m:int){this.m == this.l} implements Test {
      public def this(arg:int):Tester { property(arg,arg); }
      public def put()=0;
	}
 
    public def run()=true;
   
	
    public static def main(Rail[String]) = {
        new InterfaceTypeInvariant().execute();
    }
   

		
}
