/* Current test harness gets confused by packages, but it would be in package Clocks_For_Jocks;
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

// file Clocks line 185
class Qlocked{
static def S():void{}
static def flock() {
 val c1 = Clock.make(), c2 = Clock.make(), c3 = Clock.make();
  async clocked (c1, c2, c3) S
();
}}

class Hook {
   def run():Boolean = true;
}


public class Clocks3 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Clocks3().execute();
    }
}    
