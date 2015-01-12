/* Current test harness gets confused by packages, but it would be in package Clocks_For_Jocks_In_Clicky_Smocks;
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



public class Clocks40 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Clocks40().execute();
    }


// file Clocks line 186
 static class Example{
static def S():void{}
static def a_phase_two():void{}
static def b_phase_two():void{}
static def example() {
// ACTIVITY a
val c = Clock.make();
c.resume();
async clocked(c) {
  // ACTIVITY b
  c.advance();
  b_phase_two();
  // END OF ACTIVITY b
}
c.advance();
a_phase_two();
// END OF ACTIVITY a
} }

 static class Hook {
   def run():Boolean = true;
}

}
