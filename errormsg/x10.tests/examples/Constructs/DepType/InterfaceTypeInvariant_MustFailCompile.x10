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
import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers

public class InterfaceTypeInvariant_MustFailCompile extends x10Test { 

    public static interface Test (n:int, m: int{m==n}) {
       def put():int;
    }

    @ShouldBeErr class Tester1(n: int, m:int) implements Test{
      public def this() = { property(3,2); }
      public def put()=0;
	}
    class Tester2(n: int, m:int{self==n}) implements Test{
      @ERR public def this() = { property(3,2); }  // [Semantic Error: Invalid type; the real clause of InterfaceTypeInvariant_MustFailCompile.Tester2{self.n==3, self.m==2} is inconsistent.]
      public def put()=0;
	}
    class Tester3(n: int, m:int{self==n}) implements Test{
      public def this() = { property(3,3); }
      public def put()=0;
	}

    @ERR class Tester(l: int, m:int){m == 2 && l == 3} implements Test{ // InterfaceTypeInvariant_MustFailCompile.Tester should be declared abstract; it does not define n(): x10.lang.Int, which is declared in InterfaceTypeInvariant_MustFailCompile.Test
      public def this():Tester = { property(3,2); }
      public def put()=0;
	}
 
    public def run()=true;
   
    public static def main(Array[String](1)): void = {
        new InterfaceTypeInvariant_MustFailCompile().execute();
    }
   

		
}
