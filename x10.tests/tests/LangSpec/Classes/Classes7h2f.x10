/* Current test harness gets confused by packages, but it would be in package Classes7h2f;
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



public class Classes7h2f extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes7h2f().execute();
    }


// file Classes line 397
 static class Proper(a:Long, b:String) {
  def this(a:Long, b:String) {
      property(a, b);
  }
  def this(z:Long) {
      val theA = z+5;
      val theB = "X"+z;
      property(theA, theB);
  }
  static def example() {
      val p = new Proper(1, "one");
      assert p.a == 1 && p.b.equals("one");
      val q = new Proper(10);
      assert q.a == 15 && q.b.equals("X10");
  }
}
 static  class Hook{ def run() {Proper.example(); return true;}}

}
