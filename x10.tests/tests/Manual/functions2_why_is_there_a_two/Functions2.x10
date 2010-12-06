/* Current test harness gets confused by packages, but it would be in package functions2_why_is_there_a_two;
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

// file Functions line 69
 abstract class FunctionsTooManyFlippingFunctions[T, T1, T2]{
 abstract def arg1():T1;
 abstract def arg2():T2;
 def thing1(e:T) {var result:T;
{
  val f = (x1:T1,x2:T2){true}:T => e;
  val a1 : T1 = arg1();
  val a2 : T2 = arg2();
  result = f(a1,a2);
}
}}

class Hook {
   def run():Boolean = true;
}


public class Functions2 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Functions2().execute();
    }
}    
