/* Current test harness gets confused by packages, but it would be in package Classes_In_Poly104o;
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



public class Classes200 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Classes200().execute();
    }


// file Classes line 1299
 static  class Poly {
   public val coeff : Array[Int](1);
   public def this(coeff: Array[Int](1)) { this.coeff = coeff;}
   public def degree() = coeff.size-1;
   public def  a(i:Int) = (i<0 || i>this.degree()) ? 0 : coeff(i);

   public static operator (q:Poly) + (p:Poly) =  new Poly(
      new Array[Int](
         Math.max(q.coeff.size, p.coeff.size),
         (i:Int) => q.a(i) + p.a(i)
      ));
   public static operator (q:Poly) + (n:Int) =  q + new Poly([n as Int]);
   public static operator [T](n:T){T <: Int}: Poly = new Poly([n as Int]);
 //  public operator this + (n : Int) = new Poly([n]) + this;
   def makeSureItWorks() {
      val x = new Poly([0,1]);
      val p <: Poly = x+x+x;
      val q <: Poly = 1+x;
      val r <: Poly = x+1;
      val s  = 1+2;
      Console.OUT.println("x+x+x =" + p);
      Console.OUT.println("1+x =" + q);
      Console.OUT.println("x+1=" + r);
      Console.OUT.println("1+2 =" + s);
      
   }

 }

 static class Hook {
   def run():Boolean = true;
}

}