/* Current test harness gets confused by packages, but it would be in package Classes_explicit_type_conversions_a;
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



public class Classes230 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes230().execute();
    }


// file Classes line 2015
 static class Poly {
  public val coeff : Rail[Long];
  public def this(coeff: Rail[Long]) { this.coeff = coeff;}
  public static operator (a:Long) as ? = new Poly([a as Long]);
  public static def main(Rail[String]):void {
     val three : Poly = 3L as Poly;
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
