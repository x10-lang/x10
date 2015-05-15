/* Current test harness gets confused by packages, but it would be in package MethodResolution_yousayyouwantaresolution;
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



public class MethodResolution10 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new MethodResolution10().execute();
    }


// file Classes line 3295
 // This depends on https://jira.codehaus.org/browse/XTENLANG-2696
 static class Res {
  public static  interface Surface {}
  public static  interface Deface {}

  public static  class Ace implements Surface {
    public static operator (Boolean) : Ace = new Ace();
    public static operator (Place) : Ace = new Ace();
  }
  public static  class Face implements Surface, Deface{}

  public static  class A {}
  public static  class B extends A {}
  public static  class C extends B {}

  def m(x:A) = 0;
  def m(x:Long) = 1;
  def m(x:Boolean) = 2;
  def m(x:Surface) = 3;
  def m(x:Deface) = 4;

  def example() {
     assert m(100) == 1 : "Long";
     assert m(new C()) == 0 : "C";
     // An Ace is a Surface, unambiguous best choice
     assert m(new Ace()) == 3 : "Ace";
     // ERROR: m(new Face());

     // The match must be exact.
     // ERROR: assert m(here) == 3 : "Place";

     // Boolean could be handled directly, or by
     // implicit coercion Boolean -> Ace.
     // Direct matches always win.
     assert m(true) == 2 : "Boolean";
  }
  public static def main(argv:Rail[String]) {(new Res()).example(); Console.OUT.println("That's all!");}
 public def claim() { val ace : Ace = here; assert m(ace)==3; }
 }
 static  class Hook{ def run(){ (new Res()).example(); return true;} }

}
