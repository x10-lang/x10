/* Current test harness gets confused by packages, but it would be in package functions2_oh_no_my_ears;
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

 import x10.util.*;

public class Functions60 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Functions60().execute();
    }


// file Functions line 331
 static  class Finderator {
 static def find[T](f: (T) => Boolean, xs: x10.util.List[T], absent:T): T {
  for (x: T in xs)
    if (f(x)) return x;
  return absent;
}
 static def checkery() {
xs: List[Long] = new ArrayList[Long]();
x: Long = find((x: Long) => x>0, xs, 0);
}}

 static class Hook {
   def run():Boolean = true;
}

}
