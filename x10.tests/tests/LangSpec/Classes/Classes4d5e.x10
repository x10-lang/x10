/* Current test harness gets confused by packages, but it would be in package Classes4d5e;
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



public class Classes4d5e extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes4d5e().execute();
    }


// file Classes line 1028
 static class Two[T,U]{
  def m(x:T)=1;
  def m(x:Long)=2;
  def m[X](x:X)=3;
  def m(x:U)=4;
  static def example() {
    val t12 = new Two[Long, Any]();
    // ERROR: t12.m(2);
    val t13  = new Two[String, Any]();
    t13.m("ferret");
    val t14 = new Two[Boolean,Boolean]();
    // ERROR: t14.m(true);
  }
}
 // SKIP_NATIVE_X10: XTENLANG-1191 Native X10 does not support non-final generic instance methods

 static class Hook {
   def run():Boolean = true;
}

}
