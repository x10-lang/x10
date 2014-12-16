/* Current test harness gets confused by packages, but it would be in package statements_come_from_banks_and_cranks;
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



public class Statements60 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Statements60().execute();
    }


// file Statements line 388
 static  class LabelledBreakeyBreakyHeart {
 def findy(a:Rail[Rail[Long]], v:Long): Boolean {
var found: Boolean = false;
outer: for (i in a.range)
    for (j in a(i).range)
        if (a(i)(j) == v) {
            found = true;
            break outer;
        }
 return found;
}}

 static class Hook {
   def run():Boolean = true;
}

}
