/* Current test harness gets confused by packages, but it would be in package DefiniteAssignment4f5z;
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



public class DefiniteAssignment4f5z extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new DefiniteAssignment4f5z().execute();
    }


// file DefiniteAssignment line 71
 static  class Example {

def example(flag : Boolean) {
  var x : Long;
  if (flag) x = 1;
  // x is neither def. assigned nor unassigned.
  x = 2;
  // x is def. assigned.
 } }

 static class Hook {
   def run():Boolean = true;
}

}
