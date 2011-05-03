/* Current test harness gets confused by packages, but it would be in package classes_fields_secundus;
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



public class Classes30 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Classes30().execute();
    }


// file Classes line 198
 // NOxTEST
 static class A {
   val f = 3;
}
 static class B extends A {
   val f = 4;
 static    class C extends B {
      // C is both a subclass and inner static  class of B
      val f = 5;
       def example() {
         assert f         == 5 : "field of C";
         assert super.f   == 4 : "field of superclass";
         assert B.this.f  == 4 : "field of outer instance";
         assert B.super.f == 3 : "super.f of outer instance";
       }
    }
}
 static  class Hook { def run() { ((new B()).new C()).example(); return true; } }

}