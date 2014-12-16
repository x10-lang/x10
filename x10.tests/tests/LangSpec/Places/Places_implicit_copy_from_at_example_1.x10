/* Current test harness gets confused by packages, but it would be in package Places_implicitcopyfromat;
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



public class Places_implicit_copy_from_at_example_1 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Places_implicit_copy_from_at_example_1().execute();
    }


// file Places line 747
 static  class Example {
 static def example() {

val c = new Cell[Long](9); // (1)
at (here) {               // (2)
   assert(c() == 9);      // (3)
   c.set(8);              // (4)
   assert(c() == 8);      // (5)
}
assert(c() == 9);         // (6)
}}
 static  class Hook{ def run() { Example.example(); return true; } }

}
