/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
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

    public static interface Test {      
        public property n():int;
        public property m():int{self==this.n()};
       def put():int;
    }

    class Tester1(n: int, m:int) implements Test{
        public property n():int = n;
        @ERR public property m():int = m;
      public def this() = { property(3n,2n); }
      public def put()=0n;
	}
    class Tester2(n: int, m:int{self==this.n()}) implements Test{
        public property n():int = n;
        @ShouldNotBeERR public property m():int{self==this.n()} = m;
      @ERR @ERR public def this() = { property(3n,2n); }  // [Semantic Error: Invalid type; the real clause of InterfaceTypeInvariant_MustFailCompile.Tester2{self.n==3, self.m==2} is inconsistent.]
      public def put()=0;
	}
    class Tester3(n: int, m:int{self==this.n()}) implements Test{
        public property n():int = n;
        @ShouldNotBeERR public property m():int{self==this.n()} = m;
      public def this() = { property(3n,3n); }
      public def put()=0n;
	}

    class Tester(n: int, m:int){m == 2n && n == 3n} implements Test{ 
        public property n():int = n;
        @ERR public property m():int = m;
      public def this():Tester = { property(3n,2n); }
      public def put()=0n;
	}
 
    public def run()=true;
   
    public static def main(Rail[String]): void = {
        new InterfaceTypeInvariant_MustFailCompile().execute();
    }
   

		
}
