/* Current test harness gets confused by packages, but it would be in package Types3c3g;
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



public class Types3c3g extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types3c3g().execute();
    }


// file Types line 928
 static class ConstructorExample {
  static  class Cont[X]{}
  static  interface Inte[X]{
     def meth():X;
   }
  public static def example() {
    val a = new Cont[Int]();
    val b = new Inte[Int](){public def meth()=3;};
    type A = Cont[Int];
    val aa = new A();
    type B = Inte[Int];
    val bb = new B(){public def meth()=4;};
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
