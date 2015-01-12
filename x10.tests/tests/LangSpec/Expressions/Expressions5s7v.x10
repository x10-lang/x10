/* Current test harness gets confused by packages, but it would be in package Expressions5s7v;
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



public class Expressions5s7v extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Expressions5s7v().execute();
    }


// file Expressions line 144
 static class Fielded {
  public val a : Long = 1;
  public val b : Long{this.a == b} = this.a;
  static def example() {
    val f : Fielded = new Fielded();
    assert f.a == 1 && f.b == 1;
    val fb : Long{fb == f.a} = f.b;
    assert fb == 1;
  }
}
 static class Hook{ def run() {Fielded.example(); return true;}}

}
