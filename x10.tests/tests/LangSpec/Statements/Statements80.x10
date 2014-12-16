/* Current test harness gets confused by packages, but it would be in package Statements_AreFor_Flatements;
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



public class Statements80 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Statements80().execute();
    }


// file Statements line 552
 static  class Example {
 def example(var n:Long) {
  while (n > 1) {
     n = (n % 2 == 1) ? 3*n+1 : n/2;
  }
 } }

 static class Hook {
   def run():Boolean = true;
}

}
