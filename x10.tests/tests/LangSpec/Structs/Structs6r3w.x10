/* Current test harness gets confused by packages, but it would be in package Structs6r3w;
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



public class Structs6r3w extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Structs6r3w().execute();
    }


// file Structs line 383

 static class StructDefault {
  static  struct Example {
    val i : Long;
    def this() { i = 1; }
  }
  var ex : Example;
  static def example() {
     val ex = (new StructDefault()).ex;
     assert ex.i == 0;
  }
 }
 static   class Hook { def run() { StructDefault.example(); return true; } }

}
