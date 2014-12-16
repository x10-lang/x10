/* Current test harness gets confused by packages, but it would be in package Vars_In_Snares;
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



public class Vars30_Bad33_MustFailCompile extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Vars30_Bad33_MustFailCompile().execute();
    }


// file Vars line 156
 static  class ABitTedious{
 def example() {
val a : Long = 10;
val b = (a+1)*(a-1);
 a = 11;  // vals cannot be assigned to. // ERR
// ERROR: val a = 11; // no redeclaration.
}}

 static class Hook {
   def run():Boolean = true;
}

}
