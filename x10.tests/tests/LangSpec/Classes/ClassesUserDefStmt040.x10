/* Current test harness gets confused by packages, but it would be in package Classes_UserDefStmt_For2;
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
 *  (C) Copyright IBM Corporation 2006-2016.
 */

import harness.x10Test;

 import x10.array.DenseIterationSpace_2;

public class ClassesUserDefStmt040 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new ClassesUserDefStmt040().execute();
    }


// file Classes line 2695
 static class Parallel2 {
  public static operator for (space: DenseIterationSpace_2,
                              body: (i:Long, j:Long)=>void) {
    finish {
      for (i in space.min0 .. space.max0) {
        async for (j in space.min1 .. space.max1) {
          body(i, j);
        }
      }
    }
  }
}
 static  class Hook{
   var cpt : Long = 0;
   def run() {
     val cpt = new Cell[Long](0);
     Parallel2.for(i:Long, j:Long in 1..10 * 1..10) {
       atomic { cpt() = cpt() + i + j; }
     }
     return cpt() == 1100;
   }
 }

}
