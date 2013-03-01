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
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;



public class Classes170 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes170().execute();
    }


// file Classes line 1561

 // Integer-coefficient polynomials of one variable.
 static  class UglyPoly {
   public val coeff : Array[Int](1);
   public def this(coeff: Array[Int](1)) { this.coeff = coeff;}
   public def degree() = coeff.size-1;
   public  def  a(i:Int) = (i<0 || i>this.degree()) ? 0 : coeff(i);

   public static operator (c : Int) as UglyPoly = new UglyPoly([c as Int]);

   public def apply(x:Int) {
     val d = this.degree();
     var s : Int = this.a(d);
     for( i in 1 .. this.degree() ) {
        s = x * s + a(d-i);
     }
     return s;
   }

   public operator this + (p:UglyPoly) =  new UglyPoly(
      new Array[Int](
         Math.max(this.coeff.size, p.coeff.size),
         (i:Int) => this.a(i) + p.a(i)
      ));
   public operator this - (p:UglyPoly) = this + (-1)*p;

   public operator this * (p:UglyPoly) = new UglyPoly(
      new Array[Int](
        this.degree() + p.degree() + 1,
        (k:Int) => sumDeg(k, this, p)
        )
      );


   public operator (n : Int) + this = (n as UglyPoly) + this;
   public operator this + (n : Int) = (n as UglyPoly) + this;

   public operator (n : Int) - this = (n as UglyPoly) + (-1) * this;
   public operator this - (n : Int) = ((-n) as UglyPoly) + this;

   public operator (n : Int) * this = new UglyPoly(
      new Array[Int](
        this.degree()+1,
        (k:Int) => n * this.a(k)
      ));
   private static def sumDeg(k:Int, a:UglyPoly, b:UglyPoly) {
      var s : Int = 0;
      for( i in 0 .. k ) s += a.a(i) * b.a(k-i);
        // x10.io.Console.OUT.println("sumdeg(" + k + "," + a + "," + b + ")=" + s);
      return s;
      };
   public final def toString() = {
      var allZeroSoFar : Boolean = true;
      var s : String ="";
      for( i in 0..this.degree() ) {
        val ai = this.a(i);
        if (ai == 0) continue;
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
   private final def term(ai: Int, n:Int) = {
      val xpow = (n==0) ? "" : (n==1) ? "x" : "x^" + n ;
      return (ai == 1) ? xpow : "" + Math.abs(ai) + xpow;
   }

   def mult(p:UglyPoly) : UglyPoly = this * p;
   def mult(n:Int)      : UglyPoly = n * this;
   def plus(p:UglyPoly) : UglyPoly = this + p;
   def plus(n:Int)      : UglyPoly = n + this;
   def minus(p:UglyPoly): UglyPoly = this - p;
   def minus(n:Int)     : UglyPoly = this - n;
   static def const(n:Int): UglyPoly = n as UglyPoly;

  public static def uglymain() {
     val X = new UglyPoly([0,1]);
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
