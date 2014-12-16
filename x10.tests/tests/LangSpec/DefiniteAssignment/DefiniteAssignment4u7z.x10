/* Current test harness gets confused by packages, but it would be in package DefiniteAssignment4u7z;
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



public class DefiniteAssignment4u7z extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new DefiniteAssignment4u7z().execute();
    }


// file DefiniteAssignment line 24
 static  class Example {
def example(a:Long, b:Long) {
  val max:Long;
  //ERROR: assert max==max; // can't read 'max'
  if (a > b) max = a;
  else max = b;
  assert max >= a && max >= b;
}
}

 static class Hook {
   def run():Boolean = true;
}

}
