/* Current test harness gets confused by packages, but it would be in package state_meant_labe_L;
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

// file Statements line 171
 class Example {
 def example(a:(Int,Int) => Int, do_things_to:(Int)=>Int) {
lbl : for ([i] in 1..10) {
   for ([j] in i..10) {
      if (a(i,j) == 0) break lbl;
      if (a(i,j) == 1) continue lbl;
      if (a(i,j) == a(j,i)) break lbl;
   }
}
} }

class Hook {
   def run():Boolean = true;
}


public class Statements5 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Statements5().execute();
    }
}    
