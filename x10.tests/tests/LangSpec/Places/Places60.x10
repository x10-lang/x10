/* Current test harness gets confused by packages, but it would be in package Places_Atsome_Globref2;
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



public class Places60 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Places60().execute();
    }


// file Places line 1010
 static  class GR2 {

  public static def main(argv:Rail[String]) {
    val argref = GlobalRef[Rail[String]](argv);
    val world = Place.places();
    at(world.next(here))
        use(argref);
  }
  static def use(argref : GlobalRef[Rail[String]]) {
    at(argref) {
      val argv = argref();
      argv(0) = "Hi!";
    }
  }
}
 static  class Hook{ def run() { GR2.main(["what, me weasel?" as String]); return true; }}

}
