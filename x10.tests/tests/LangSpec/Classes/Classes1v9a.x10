/* Current test harness gets confused by packages, but it would be in package Classes1v9a;
*/
// Warning: This file is auto-generated from the TeX source of the language spec.
// If you need it changed, work with the specification writers.


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

import harness.x10Test;



public class Classes1v9a extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes1v9a().execute();
    }


// file Classes line 1336
 static class Ret(n:Long) {
   def this()    { property(1); }     // (A)
   def this(n:Long) : Ret{n==self.n} { // (B)
      property(n);
   }
   static def typeIs[T](x:T){}
   static def example() {
     typeIs[Ret{self.n==1}](new Ret());  // uses (A)
     typeIs[Ret{self.n==3}](new Ret(3)); // uses (B)
   }
}

 static class Hook {
   def run():Boolean = true;
}

}
