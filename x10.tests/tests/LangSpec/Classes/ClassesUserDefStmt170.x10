/* Current test harness gets confused by packages, but it would be in package Classes_UserDefStmt_While2;
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

public class ClassesUserDefStmt170 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new ClassesUserDefStmt170().execute();
    }


// file Classes line 3422
 static  class Timeout {
   public static operator while(ms: Long, body: ()=>void) {
     val deadline = System.currentTimeMillis() + ms;
     while (System.currentTimeMillis() < deadline) {
       body();
     }
   }
 }
 static  class Test {
public static def main(Rail[String]) {
  val cpt = new Cell[Long](0);
  Timeout.while(10) {
    atomic { cpt() = cpt() + 1; }
  }
  Console.OUT.println(cpt());
}
 }

 static class Hook {
   def run():Boolean = true;
}

}
