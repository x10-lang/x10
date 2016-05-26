/* Current test harness gets confused by packages, but it would be in package Classes_UserDefStmt_Async1;
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



public class ClassesUserDefStmt070 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new ClassesUserDefStmt070().execute();
    }


// file Classes line 2951
 static class Escape {
  private var task: ()=>void = null;
  private var stop: Boolean = false;

  public def this() {
    async {
      while (!stop) {
        val t: () => void;
        when (task != null || stop) {
          t = task;
          task = null;
        }
        if (t != null) {
          async { t(); }
        }
      }
    }
  }

  public operator async (body: () => void) {
    when (task == null) {
      task = body;
    }
  }

  public def stop() {
    atomic { stop = true; }
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
