/* Current test harness gets confused by packages, but it would be in package Classes_In_Remedial_Poly101;
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



public class Classes170 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes170().execute();
    }


// file Classes line 1560

 // Integer-coefficient polynomials of one variable.
 static  class UglyPoly {
   public val coeff : Rail[Long];
   public def this(coeff: Rail[Long]) { this.coeff = coeff;}
   public def degree() = coeff.size-1;
   public  def  a(i:Long) = (i<0 || i>this.degree()) ? 0L : coeff(i);

   public static operator (c : Long) as UglyPoly = new UglyPoly([c as Long]);

   public def apply(x:Long) {
     val d = this.degree();
     var s : Long = this.a(d);
     for( i in 1L .. this.degree() ) {
        s = x * s + a(d-i);
     }
     return s;
   }

   public operator this + (p:UglyPoly) =  new UglyPoly(
      new Rail[Long](
         Math.max(this.coeff.size, p.coeff.size),
         (i:Long) => this.a(i) + p.a(i)
      ));
   public operator this - (p:UglyPoly) = this + (-1)*p;

   public operator this * (p:UglyPoly) = new UglyPoly(
      new Rail[Long](
        this.degree() + p.degree() + 1,
        (k:Long) => sumDeg(k, this, p)
        )
      );


   public operator (n : Long) + this = (n as UglyPoly) + this;
   public operator this + (n : Long) = (n as UglyPoly) + this;

   public operator (n : Long) - this = (n as UglyPoly) + (-1) * this;
   public operator this - (n : Long) = ((-n) as UglyPoly) + this;

   public operator (n : Long) * this = new UglyPoly(
      new Rail[Long](
        this.degree()+1,
        (k:Long) => n * this.a(k)
      ));
   private static def sumDeg(k:Long, a:UglyPoly, b:UglyPoly) {
      var s : Long = 0;
      for( i in 0L .. k ) s += a.a(i) * b.a(k-i);
        // x10.io.Console.OUT.println("sumdeg(" + k + "," + a + "," + b + ")=" + s);
      return s;
      };
   public final def toString() = {
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
   private final def term(ai: Long, n:Long) = {
      val xpow = (n==0L) ? "" : (n==1L) ? "x" : "x^" + n ;
      return (ai == 1L) ? xpow : "" + Math.abs(ai) + xpow;
   }

   def mult(p:UglyPoly) : UglyPoly = this * p;
   def mult(n:Long)     : UglyPoly = n * this;
   def plus(p:UglyPoly) : UglyPoly = this + p;
   def plus(n:Long)     : UglyPoly = n + this;
   def minus(p:UglyPoly): UglyPoly = this - p;
   def minus(n:Long)    : UglyPoly = this - n;
   static def const(n:Long): UglyPoly = n as UglyPoly;

  public static def uglymain() {
     val X = new UglyPoly([0L,1L]);
     val t <: UglyPoly
           = X.mult(7).plus(
               X.mult(X).mult(X).mult(6));
     val u <: UglyPoly
           = const(3).plus(
               X.mult(5)).minus(X.mult(X).mult(7));
     val v <: UglyPoly = t.mult(u).minus(1);
     for( i in -3 .. 3) {
       x10.io.Console.OUT.println(
         "" + i + "	X:" + X.apply(i) + "	t:" + t.apply(i)
          + "	u:" + u.apply(i) + "	v:" + v.apply(i)
         );
     }
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
