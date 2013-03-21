/* Current test harness gets confused by packages, but it would be in package Expressions_RailCtor_Details;
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



public class Expressions170 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Expressions170().execute();
    }


// file Expressions line 1939
 static class Eel{}
 static class Example{
def example(){
val zero <: Rail[Int{self == 0}]
          = [0];
val non1 <: Rail[Int{self != 1}]
          = [0 as Int{self != 1}];
val eels <: Rail[Eel{self != null}]
          = [new Eel() as Eel{self != null},
             new Eel(), new Eel()];
}}

 static class Hook {
   def run():Boolean = true;
}

}
