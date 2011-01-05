/* Current test harness gets confused by packages, but it would be in package Classes_Assignments1_oddvec;
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



public class Classes250 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Classes250().execute();
    }


// file Classes line 1483
 static class Oddvec {
  var v : Array[Int](1) = new Array[Int](3, (Int)=>0);
  public def apply() = "(" + v(0) + "," + v(1) + "," + v(2) + ")";
  public def apply(i:Int) = v(i);
  public def apply(i:Int, j:Int) = [v(i),v(j)];
  public def set(newval:Int, i:Int) = {v(i) = newval;}
  public def set(newval:Int, i:Int, j:Int) = {
       v(i) = newval; v(j) = newval+1;}
  // ...
  public static def main(argv:Rail[String]):void {
     val a = new Oddvec();
     x10.io.Console.OUT.println(a() + " ... " + a(0));
     a(1) = 20;
     x10.io.Console.OUT.println(a());
     a(0) = 30;
     x10.io.Console.OUT.println(a());
     a(0,1) = 100;
     x10.io.Console.OUT.println(a());
   }
 }

 static class Hook {
   def run():Boolean = true;
}

}