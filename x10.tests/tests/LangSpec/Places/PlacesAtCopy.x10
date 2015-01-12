/* Current test harness gets confused by packages, but it would be in package Places_AtCopy2;
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



public class PlacesAtCopy extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new PlacesAtCopy().execute();
    }


// file Places line 823
 static class example {
static def Example() {

val c = new Cell[Long](5);
val a : Rail[Cell[Long]] = [c,c as Cell[Long]];
assert(a(0)() == 5 && a(1)() == 5);     // (A)
c.set(6);                               // (B)
assert(a(0)() == 6 && a(1)() == 6);     // (C)
at(here) {
  assert(a(0)() == 6 && a(1)() == 6);   // (D)
  c.set(7);                             // (E)
  assert(a(0)() == 7 && a(1)() == 7);   // (F)
}
assert(a(0)() == 6 && a(1)() == 6);     // (G)
}}
 static class Hook{ def run() { example.Example(); return true; } }

}
