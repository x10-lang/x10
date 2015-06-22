/* Current test harness gets confused by packages, but it would be in package Places_copyingblockingwithglobref;
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



public class Places50 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Places50().execute();
    }


// file Places line 950
 static  class GR {
  static def use(Any){}
  static def example() {

val huge = "A potentially big thing";
val href = GlobalRef(huge);
at (here) {
   use(href);
  }
}
}
 static  class Hook{ def run() { GR.example(); return true; } }

}
