/* Current test harness gets confused by packages, but it would be in package Activities5m7u;
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
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;



public class Activities5m7u extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Activities5m7u().execute();
    }


// file Activities line 647
 static  class Example {
static def good() {
  val c = new Cell[Boolean](false);
  async {
    atomic {c() = true;}
  }
  when( c() );
}
static def bad() {
  val c = new Cell[Boolean](false);
  async {
    c() = true;
  }
  when( c() );
}
 }

 static class Hook {
   def run():Boolean = true;
}

}
