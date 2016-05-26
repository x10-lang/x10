/* Current test harness gets confused by packages, but it would be in package Classes_UserDefStmt_Finish2;
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

public class ClassesUserDefStmt120 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new ClassesUserDefStmt120().execute();
    }


// file Classes line 3197
 static  class SignalingFinish {
   private var terminated : Boolean = false;
   public operator finish(body: ()=>void) {
     finish {
       body();
     }
     atomic { terminated = true; }
   }
   public def join() {
     when (terminated) {}
   }
 }
 static  class Test {
public static def main(Rail[String]) {
  val t = new SignalingFinish();
  async {
    t.join();
    Console.OUT.println("after");
  }
  t.finish {
    Console.OUT.println("before");
  }
}
 }

 static class Hook {
   def run():Boolean = true;
}

}
