/* Current test harness gets confused by packages, but it would be in package Places_transient_a;
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



public class Places40_Bad33_MustFailCompile extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Places40_Bad33_MustFailCompile().execute();
    }


// file Places line 891

 static class Trans {
   val a : Long = 1;
   transient val b : Long = 2;
 transient val c : Long{c != 0} = 3; // ERR
   def example() {
     assert(a == 1 && b == 2);
     at(here) {
        assert(a == 1 && b == 0);
     }
   }
}
 static class Hook{ def run() { (new Trans()).example(); return true; } }

}
