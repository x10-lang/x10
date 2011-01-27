/* Current test harness gets confused by packages, but it would be in package Statements4h6p;
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
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;



public class Statements4h6p extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Statements4h6p().execute();
    }


// file Statements line 93
 static class Shadow{
  var x : Int;
  def this(x:Int) {
     // Parameter can shadow field
     this.x = x;
  }
  def example(y:Int) {
     val x = "shadows a field";
     // ERROR: val y = "shadows a param";
     val z = "local";
     for (a in [1,2,3]) {
        // ERROR: val x = "can't shadow local var";
     }
     async {
        val x = "can shadow through async";
     }
     at (here) {
        val x = "can shadow through at";
     }
     val f = () => {
       val x = "can shadow through closure";
       x
     };
  }
}

 static class Hook {
   def run():Boolean = true;
}

}