package eg;
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

/* Sorry about the package line -- but this is a failure of the package system; 
it compiles fine without the package.    It is the example of obscuring from the 
spec -- that's the 'struct eg' and beyond -- with an additional 'package eg;' on top
plus test harness.  It should compile and execute fine.  It currently does not.
*/

import x10.util.*;
import harness.x10Test;
import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers

public class StructObscuringExample_MustFailCompile extends x10Test { 
  public static def main(Rail[String]){
     val p = new StructObscuringExample_MustFailCompile();
     p.execute();
  }
  public def run():Boolean {
     eg().example();
     return true;

  }
}


struct eg {
   static def ow()= 1;
   static struct Bite {
      def ow() = 2;
   }
   def example() {
       val eg = Bite();
       assert eg.ow() == 2;
       assert eg.eg.ow() == 1; // ERR: Field eg not found in type "eg.eg.Bite{self==eg}"
   }
}
