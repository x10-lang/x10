/* Current test harness gets confused by packages, but it would be in package ObjectInitialization_ShowingSegments;
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



public class ObjectInitialization20 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new ObjectInitialization20().execute();
    }


// file Classes line 2936
 static class Overlord(x:Long) {
  def this(x:Long) { property(x); }
}//Overlord
 static class Overdone(y:Long) extends Overlord  {
  val a : Long;
  val b =  y * 9000;
  def this(r:Long) {
    super(r);                      // (1)
    x10.io.Console.OUT.println(r); // (2)
    val rp1 = r+1;
    property(rp1);                 // (2)
    // field initializations here  // (3)
    a = r + 2 + b;                 // (4)
  }
  def this() {
    this(10);                      // (1), (2), (3)
    val x = a + b;                 // (4)
  }
}//Overdone

 static class Hook {
   def run():Boolean = true;
}

}
