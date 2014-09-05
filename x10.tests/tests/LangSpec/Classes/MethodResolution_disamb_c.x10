/* Current test harness gets confused by packages, but it would be in package MethodResolution_disamb_c;
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



public class MethodResolution_disamb_c extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new MethodResolution_disamb_c().execute();
    }


// file Classes line 3771

 static class Disambig {
  public val f : (Long)=>Long =  (x:Long) => x*x;
  public def f(y:int) = y+1;
  public def example() {
      assert(  this.f(10)  == 11  );
      assert( (this.f)(10) == 100 );
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
