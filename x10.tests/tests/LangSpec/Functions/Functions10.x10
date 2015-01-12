/* Current test harness gets confused by packages, but it would be in package Functions10;
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



public class Functions10 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Functions10().execute();
    }


// file Functions line 37
 static  class Examplllll {
 static
val sq: (Long) => Long
      = (n:Long) => {
           var s : Long = 0;
           val abs_n = n < 0 ? -n : n;
           for (i in 1..abs_n) s += abs_n;
           s
        };
}
 static  class Hook{ def run() { return Examplllll.sq(5) == 25; } }

}
