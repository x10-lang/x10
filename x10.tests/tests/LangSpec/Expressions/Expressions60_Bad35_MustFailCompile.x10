/* Current test harness gets confused by packages, but it would be in package Expressions_Calls_Guarded_By_Walls;
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



public class Expressions60_Bad35_MustFailCompile extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Expressions60_Bad35_MustFailCompile().execute();
    }


// file Expressions line 349
//OPTIONS: -STATIC_CHECKS
 static class DivideBy(denom:Long) {
  def div(numer:Long){denom != 0} = numer / denom;
  def example() {
     val thisCast = (this as DivideBy{self.denom != 0});
     thisCast.div(100);
 this.div(100); // ERR
  }
}
 static  class Hook{ def run() { (new DivideBy(1)).example(); return true; } }

}
