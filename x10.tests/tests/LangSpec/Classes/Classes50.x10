/* Current test harness gets confused by packages, but it would be in package Classes_Toss_Freedom_Disk2;
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



public class Classes50 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes50().execute();
    }


// file Classes line 340
 static class Proper(t:Long) {
  def this(t:Long) {property(t);}
}
 static  class Hook{ def run() {
   val p = new Proper(4);
   return p.t == 4;
 } }

}
