/* Current test harness gets confused by packages, but it would be in package InnerClasses5p9v;
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



public class InnerClasses5p9v extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new InnerClasses5p9v().execute();
    }


// file InnerClasses line 231
 static class Outer {
  val a = 1;
  def m() {
    val a = -2;
    val b = 2;
 static     class Local {
      val a = 3;
      def m() = 100*Outer.this.a + 10*b + a;
    }
    val l : Local = new Local();
    assert l.m() == 123;
  }//end of m()
}
 static  class Hook{ def run() {
   val o <: Outer = new Outer();
   o.m();
   return true;
 } }

}