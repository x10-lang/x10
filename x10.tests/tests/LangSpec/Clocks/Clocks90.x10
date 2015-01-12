/* Current test harness gets confused by packages, but it would be in package Clocks_ClockedFinish;
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



public class Clocks90 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Clocks90().execute();
    }


// file Clocks line 395
 static class Example{
static def phase(String, Long) {}
def example() {
clocked finish {
  clocked async {
     phase("A", 1);
     Clock.advanceAll();
     phase("A", 2);
  }
  clocked async {
     phase("B", 1);
     Clock.advanceAll();
     phase("B", 2);
  }
}
}}

 static class Hook {
   def run():Boolean = true;
}

}
