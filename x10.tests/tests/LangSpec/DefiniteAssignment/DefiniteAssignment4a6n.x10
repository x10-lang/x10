/* Current test harness gets confused by packages, but it would be in package DefiniteAssignment4a6n;
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



public class DefiniteAssignment4a6n extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new DefiniteAssignment4a6n().execute();
    }


// file DefiniteAssignment line 130
 static  class Example {
 def example() {
val a : Long;
finish {
  async {
    a = 1;
  }
  // a is not definitely assigned here
}
// a is definitely assigned after 'finish'
assert a==1;
} }

 static class Hook {
   def run():Boolean = true;
}

}
