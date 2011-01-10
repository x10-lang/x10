/* Current test harness gets confused by packages, but it would be in package Vars_Local_lyproducedproduce;
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



public class Vars110 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Vars110().execute();
    }


// file Vars line 443
 static  class NotInitVal {
static def main(r: Array[String](1)):void {
  val a : Int;
  a = r.size;
  val b : String;
  if (a == 5) b = "five?"; else b = "" + a + " args";
  // ...
} }

 static class Hook {
   def run():Boolean = true;
}

}