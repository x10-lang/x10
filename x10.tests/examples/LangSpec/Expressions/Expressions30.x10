/* Current test harness gets confused by packages, but it would be in package exp_re_ssio_ns_fiel_dacc_ess;
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



public class Expressions30 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Expressions30().execute();
    }


// file Expressions line 187
 //NObTEST
 static class Uncle {
  public static val f = 1;
}
 static class Parent {
  public val f = 2;
}
 static class Ego extends Parent {
  public val f = 3;
 static   class Child extends Ego {
     public val f = 4;
     def example() {
        assert Uncle.f == 1;
        assert Ego.super.f == 2;
        assert super.f == 3;
        assert this.f == 4;
        assert f == 4;
     }
  }
}
 static class Hook{ def run() {
  val ego = new Ego();
  val child = ego.new Child();
  child.example();
  return true;
 } }

}