/* Current test harness gets confused by packages, but it would be in package Classes_SimplifiedRegion;
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

import x10.regionarray.*;

public class Classes280 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes280().execute();
    }


// file Classes line 2402
 static class MyRegion(rank:Long) {
  static type MyRegion(n:Long)=MyRegion{rank==n};
  def this(r:Long):MyRegion(r) {
    property(r);
  }
  def this(diag:Rail[Long]):MyRegion(diag.size){
    property(diag.size);
  }
  def mockUnion(r:MyRegion(rank)):MyRegion(rank) = this;
  def example() {
    val R1 : MyRegion(3L) = new MyRegion([4,4,4 as Long]);
    val R2 : MyRegion(3L) = new MyRegion([5,4,1]);
    val R3 = R1.mockUnion(R2); // inferred type MyRegion(3)
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
