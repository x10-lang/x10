/* Current test harness gets confused by packages, but it would be in package Vars_For_Squares;
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



public class Vars10 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Vars10().execute();
    }


// file Vars line 51
 static class Counter {
  private var n : Long = 0;
  public def bump() : Long {
    val nxt = n+1;
    n = nxt;
    return nxt;
    }
}
 static  class Hook{ def run() { val c = new Counter(); val d = new Counter();
   assert c.bump() == 1;
   assert c.bump() == 2;
   assert c.bump() == 3;
   assert c.bump() == 4;
   assert d.bump() == 1;
   assert c.bump() == 5;
   return true;
 } }

}
