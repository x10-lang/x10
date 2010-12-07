/* Current test harness gets confused by packages, but it would be in package ObjectInit_GrandExample;
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

// file ObjectInitialization line 539
 class Supertype[T]{}
 interface SuperInterface[T]{}
class Example (
   prop : Int,
   proq : Int{prop != proq},                    // (A)
   pror : Int
   )
   {prop != 0}                                  // (B)
   extends Supertype[Int{self != prop}]         // (C)
   implements SuperInterface[Int{self != prop}] // (D)
{
   property def propmeth() = (prop == pror);    // (E)
   static staticField
      : Cell[Int{self != 0}]                    // (F)
      = new Cell[Int{self != 0}](1);            // (G)
   var instanceField
      : Int {self != prop}                      // (H)
      = (prop + 1) as Int{self != prop};        // (I)
   def this(
      a : Int{a != 0},
      b : Int{b != a}                           // (J)
      )
      {a != b}                                  // (K)
      : Example{self.prop == a && self.proq==b} // (L)
   {
      super();                                  // (M)
      property(a,b,a);                          // (N)
      // fields initialized here
      instanceField = b as Int{self != prop};   // (O)
   }

   def someMethod() =
        prop + staticField + instanceField;     // (P)
}

class Hook {
   def run():Boolean = true;
}


public class ObjectInitialization6 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new ObjectInitialization6().execute();
    }
}    
