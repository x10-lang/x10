/* Current test harness gets confused by packages, but it would be in package Classes_In_Poly105;
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



public class Classes210 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes210().execute();
    }


// file Classes line 1901
 static  class Poly {
   public val coeff : Rail[Long];
   public def this(coeff: Rail[Long]) { this.coeff = coeff;}
   public def degree() = coeff.size-1;
   public def  a(i:Long) = (i<0 || i>this.degree()) ? 0L : coeff(i);
  public static operator (p:Poly) + (q:Poly) =  new Poly(
     new Rail[Long](
        Math.max(q.coeff.size, p.coeff.size),
        (i:Long) => q.a(i) + p.a(i)
     ));

   public operator (n : Long) + this = new Poly([n as Long]) + this;
   public operator this + (n : Long) = new Poly([n as Long]) + this;

   def makeSureItWorks() {
      val x = new Poly([0L,1L]);
      val p <: Poly = x+x+x;
      val q <: Poly = 1n+x;
      val r <: Poly = x+1n;
      val s <: Poly = 1+x;
      val t <: Poly = x+1;
   }

 }

 static class Hook {
   def run():Boolean = true;
}

}
