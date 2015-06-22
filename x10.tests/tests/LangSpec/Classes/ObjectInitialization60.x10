/* Current test harness gets confused by packages, but it would be in package ObjectInit_GrandExample;
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
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;



public class ObjectInitialization60 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new ObjectInitialization60().execute();
    }


// file Classes line 3238
 static  class Supertype[T]{}
 static  interface SuperInterface[T]{}
 static class Example (
   prop : Long,
   proq : Long{prop != proq},                    // (A)
   pror : Long
   )
   {prop != 0}                                  // (B)
   extends Supertype[Long{self != prop}]         // (C)
   implements SuperInterface[Long{self != prop}] // (D)
{
   property def propmeth() = (prop == pror);    // (E)
   staticField
      : Cell[Long{self != 0}]                    // (F)
      = new Cell[Long{self != 0}](1);            // (G)
   var instanceField
      : Long {self != prop}                      // (H)
      = (prop + 1) as Long{self != prop};        // (I)
   def this(
      a : Long{a != 0},
      b : Long{b != a}                           // (J)
      )
      {a != b}                                  // (K)
      : Example{self.prop == a && self.proq==b} // (L)
   {
      super();                                  // (M)
      property(a,b,a);                          // (N)
      // fields initialized here
      instanceField = b as Long{self != prop};   // (O)
   }

   def someMethod() =
        prop + staticField() + instanceField;   // (P)
}

 static class Hook {
   def run():Boolean = true;
}

}
