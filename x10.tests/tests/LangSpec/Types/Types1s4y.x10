/* Current test harness gets confused by packages, but it would be in package Types1s4y;
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
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

 import x10.regionarray.*;
import x10.util.*;

public class Types1s4y extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types1s4y().execute();
    }


// file Types line 2830
 static class Cl[C1, C2, C3]{}
 static class Example {
  static def me[X1, X2](Cl[Long, X1, X2]) =
     new Cl[X1, X2, Point]();
  static def example() {
    val a = new Cl[Long, Boolean, String]();
    val b : Cl[Boolean, String, Point]
          = me[Boolean, String](a);
    val c : Cl[Boolean, String, Point]
          = me(a);
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
