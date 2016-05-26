/* Current test harness gets confused by packages, but it would be in package Classes_UserDefStmt_For1;
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

 import x10.util.*;

public class ClassesUserDefStmt030 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new ClassesUserDefStmt030().execute();
    }


// file Classes line 2653
 static class Parallel {

  public static operator for[T](c: Iterable[T], body: (T)=>void) {
    finish {
      for(x in c) {
        async { body(x); }
      }
    }
  }

  public static def main(Rail[String]) {
    val cpt = new Cell[Long](0);
    Parallel.for(i:Long in 1..10) {
      atomic { cpt() = cpt() + i; }
    }
    Console.OUT.println(cpt());
  }
}
 static  class Hook{
   var cpt : Long = 0;
   def run() {
     val cpt = new Cell[Long](0);
     Parallel.for(i:Long in 1..10) {
       atomic { cpt() = cpt() + i; }
     }
     return cpt() == 55;
   }
 }

}
