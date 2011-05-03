/* Current test harness gets confused by packages, but it would be in package exp_vexp_pexp_lexp_shexp;
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



public class Expressions10 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Expressions10().execute();
    }


// file Expressions line 51
 //NOaTEST
 static class Outer {
  val three = 3;
 static   class Inner {
     val three = "THREE";
     def example()  {
       assert Outer.this.three == 3;
       assert three.equals("THREE");
       assert this.three.equals("THREE");
     }
  }
}
 static  class Hook{ def run(){
   val o = new Outer();
   val i = o.new Inner();
   i.example();
   return true;
 } }

}