/* Current test harness gets confused by packages, but it would be in package statements_FOR_block_heads;
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



public class Statements30 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Statements30().execute();
    }


// file Statements line 222
 static  class Example {
 def example(b:Boolean, S1:(Long)=>void, S2:(Long)=>void ) {
if (b) {
  // This is a block
  val v = 1;
  S1(v);
  S2(v);
}
  } }

 static class Hook {
   def run():Boolean = true;
}

}
