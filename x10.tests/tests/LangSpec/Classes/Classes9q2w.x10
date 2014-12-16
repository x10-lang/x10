/* Current test harness gets confused by packages, but it would be in package Classes9q2w;
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



public class Classes9q2w extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes9q2w().execute();
    }


// file Classes line 612
 static class Ret(n:Long) {
  static def met1(a:Long) : Ret{n==a} = new Ret(a);
  static def met2(a:Long)             = new Ret(a);
  static def use3(Ret{n==3}) {}
  static def example() {
     use3(met1(3));
     use3(met2(3));
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
