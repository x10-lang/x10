/* Current test harness gets confused by packages, but it would be in package Clocks_ClockedFinish;
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

// file Clocks line 429
class Example{
static def phase(String, Int) {}
def example() {
clocked finish {
  clocked async {
     phase("A", 1);
     next;
     phase("A", 2);
  }
  clocked async {
     phase("B", 1);
     next;
     phase("B", 2);
  }
}
}}

class Hook {
   def run():Boolean = true;
}


public class Clocks9 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Clocks9().execute();
    }
}    
