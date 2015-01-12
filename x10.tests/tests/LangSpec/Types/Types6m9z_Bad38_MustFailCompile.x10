/* Current test harness gets confused by packages, but it would be in package Types6m9z_Bad38_MustFailCompile;
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
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;



public class Types6m9z_Bad38_MustFailCompile extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types6m9z_Bad38_MustFailCompile().execute();
    }


// file Types line 3049
//OPTIONS: -STATIC_CHECKS
 static class Extra(n:Long) {
  val f : Long;
  def this(n:Long, f:Long) { property(n); this.f = f; }
  static def typeIs[T](val x:T) {}
  public static def main(argv:Rail[String]) {
     val x : Extra = new Extra(1,2L);
     typeIs[ Extra{self==x} ]   (x);    //(A)
     val nx: Extra = new Extra(1,2L);
 typeIs[ Extra{self==x} ]   (nx); //(!A) // ERR
     typeIs[ Long{self == x.f} ]          (x.f);  //(B)
     val y : Extra{self.n==8} = new Extra(8, 4L);
     typeIs[ Long{self == y.f, y.n == 8}] (y.f);  //(C)
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
