/* Current test harness gets confused by packages, but it would be in package Classes_UserDefStmt_If3;
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

public class ClassesUserDefStmt025 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new ClassesUserDefStmt025().execute();
    }


// file Classes line 2441
 static  class RandomIf {
     val random = new Random();
     public operator if(then: ()=>void, else_: ()=>void) {
         if (random.nextBoolean()) {
             then();
         } else {
             else_();
         }
     }
 }
 static  class Test2 {
   def test() {
    val random = new RandomIf();
    random.operator if (() => { Console.OUT.println("true"); },
                        () => { Console.OUT.println("false"); });
   }
 }

 static class Hook {
   def run():Boolean = true;
}

}
