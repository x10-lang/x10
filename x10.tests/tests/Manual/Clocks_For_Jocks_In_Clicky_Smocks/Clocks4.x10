/* Current test harness gets confused by packages, but it would be in package Clocks_For_Jocks_In_Clicky_Smocks;
*/

/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

// file Clocks line 214
class Example{
static def S():void{}
static def a_phase_two():void{}
static def b_phase_two():void{}
static def example() {
// a
val c = Clock.make();
c.resume();
async clocked(c) {
  // b
  c.next();
  b_phase_two();
}
c.next();
a_phase_two();
} }

class Hook {
   def run():Boolean = true;
}


public class Clocks4 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Clocks4().execute();
    }
}    
