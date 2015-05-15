/* Current test harness gets confused by packages, but it would be in package Classes_And_Implicit_Coercions;
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



public class Classes240 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes240().execute();
    }


// file Classes line 2085
 static  class Poly {
   public val coeff : Rail[Long];
   public def this(coeff: Rail[Long]) { this.coeff = coeff;}
   public def degree() = (coeff.size-1);
   public def  a(i:Long) = (i<0 || i>this.degree()) ? 0L : coeff(i);
   public final def toString() {
      var allZeroSoFar : Boolean = true;
      var s : String ="";
      for( i in 0L..this.degree() ) {
        val ai = this.a(i);
        if (ai == 0L) continue;
        if (allZeroSoFar) {
           allZeroSoFar = false;
           s = term(ai, i);
        }
        else
           s +=
              (ai > 0 ? " + " : " - ")
             +term(ai, i);
      }
      if (allZeroSoFar) s = "0";
      return s;
   }
   private final def term(ai: Long, n:Long) {
      val xpow = (n==0L) ? "" : (n==1L) ? "x" : "x^" + n ;
      return (ai == 1L) ? xpow : "" + Math.abs(ai) + xpow;
   }

  public static operator (c : Long) : Poly
     = new Poly([c as Long]);

  public static operator (p:Poly) + (q:Poly) = new Poly(
      new Rail[Long](
        Math.max(p.coeff.size, q.coeff.size),
        (i:Long) => p.a(i) + q.a(i)
     ));

  public static def main(Rail[String]):void {
     val x = new Poly([0L,1L]);
     x10.io.Console.OUT.println("1+x=" + (1L+x));
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
