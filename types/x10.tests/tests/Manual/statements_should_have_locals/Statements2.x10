/* Current test harness gets confused by packages, but it would be in package statements_should_have_locals;
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

// file Statements line 57
 class LocalExample {
 def example(a:Int) {
  if (a > 1) {
     val b = a/2;
     var c : Int = 0;
     // b and c are defined here
  }
  // b and c are not defined here.
} }

class Hook {
   def run():Boolean = true;
}


public class Statements2 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Statements2().execute();
    }
}    
