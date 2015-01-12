/* Current test harness gets confused by packages, but it would be in package Classes_field_init_expr_a;
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



public class Classes10 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes10().execute();
    }


// file Classes line 151
 static class Fld{
  val a = 1;
  val b = 2+a;
}
 static  class Hook{ def run() {
   val f = new Fld();
   assert f.a == 1 && f.b == 3;
   return true;}}

}
