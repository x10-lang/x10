/* Current test harness gets confused by packages, but it would be in package oifClasses6a1j;
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



public class Classes6a1j extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes6a1j().execute();
    }


// file Classes line 1722
 static  class Whatever {

static  class Trace(n:Long){
  public static operator !(f:Trace)
      = new Trace(10 * f.n + 1);
  public operator -this = new Trace (10 * this.n + 2);
}
static  class Brace extends Trace{
  def this(n:Long) { super(n); }
  public operator -this = new Brace (10 * this.n + 3);
  static def example() {
     val t = new Trace(1);
     assert (!t).n == 11;
     assert (-t).n == 12 && (-t instanceof Trace);
     val b = new Brace(1);
     assert (!b).n == 11;
     assert (-b).n == 13 && (-b instanceof Brace);
  }
}

 // And checking the unambiguous syntax while I'm here...
 //static  class Glook { def checky(t:Trace) {
 //   Trace.operator !(t);
 //   t.operator -();
 //} }
 }

 static class Hook {
   def run():Boolean = true;
}

}
