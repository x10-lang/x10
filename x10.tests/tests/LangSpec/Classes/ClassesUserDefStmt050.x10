/* Current test harness gets confused by packages, but it would be in package Classes_UserDefStmt_Try1;
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

public class ClassesUserDefStmt050 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new ClassesUserDefStmt050().execute();
    }


// file Classes line 2812
 static class Flatten {

  public static operator try(body:()=>void,
                             handler:(MultipleExceptions)=>void) {
    try { body(); }
    catch (me: MultipleExceptions) {
      val exns = new GrowableRail[CheckedThrowable]();
      flatten(me, exns);
      handler (new MultipleExceptions(exns));
    }
  }

  private static def flatten(me:MultipleExceptions,
                             acc:GrowableRail[CheckedThrowable]) {
    for (e in me.exceptions) {
      if (e instanceof MultipleExceptions) {
        flatten(e as MultipleExceptions, acc);
      } else {
        acc.add(e);
      }
    }
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
