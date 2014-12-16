/* Current test harness gets confused by packages, but it would be in package Expressions2p4c;
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



public class Expressions2p4c extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Expressions2p4c().execute();
    }


// file Expressions line 1292
 static class Knot(s:String){
  public def is(t:String):Boolean = s.equals(t);
  // explicit conversion
  public static operator (n:Long) as Knot = new Knot("knot-" + n);
  // implicit coercion
  public static operator (s:String):Knot = new Knot(s);
  // using them
  public static def example() {
     val a : Knot = 1 as Knot;
     val b : Knot = "frayed";
     val c : Knot = "three" as Knot;
     assert a.is("knot-1") && b.is("frayed") && c.is("three");
  }
}
 static  class Hook{ def run() {Knot.example(); return true;}}

}
