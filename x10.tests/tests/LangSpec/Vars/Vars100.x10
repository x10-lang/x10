/* Current test harness gets confused by packages, but it would be in package Vars_For_Squares_Of_Mares;
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



public class Vars100 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Vars100().execute();
    }


// file Vars line 433
 static  class Example {
static def inc(var i:Long) { i += 1; }
static def example() {
   var j : Long = 0;
   assert j == 0;
   inc(j);
   assert j == 0;
}
}
 static  class Hook{ def run() {Example.example(); return true;}}

}
