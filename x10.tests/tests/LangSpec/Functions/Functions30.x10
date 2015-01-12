/* Current test harness gets confused by packages, but it would be in package functions2_b;
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



public class Functions30 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Functions30().execute();
    }


// file Functions line 87
 abstract static  class FunctionsTooManyFlippingFunctions[T, T1, T2]{
 abstract def arg1():T1;
 abstract def arg2():T2;
 def thing1(e:T) {
var result:T;
{
  val a1 : T1 = arg1();
  val a2 : T2 = arg2();
  {
     val x1 : T1 = a1;
     val x2 : T2 = a2;
     result = e;
  }
}
}}

 static class Hook {
   def run():Boolean = true;
}

}
