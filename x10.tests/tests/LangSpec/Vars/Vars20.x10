/* Current test harness gets confused by packages, but it would be in package Vars_For_Bears_In_Chairs;
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



public class Vars20 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Vars20().execute();
    }


// file Vars line 103
 static class VarExample{
static def example() {
val a : Long = 0;               // Full 'val' syntax
b : Long = 0;                   // 'val' implied
val c = 0;                     // Type inferred
var d : Long = 0;               // Full 'var' syntax
var e : Long;                   // Not initialized
var f : Long{self != 100} = 0;  // Constrained type
val g : Long;                   // Init. deferred
if (a > b) g = 1; else g = 2;  // Init. done here.
}}

 static class Hook {
   def run():Boolean = true;
}

}
