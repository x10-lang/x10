/* Current test harness gets confused by packages, but it would be in package Arrays_SimpleArrays_Example2;
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

 import x10.array.*;

public class SimpleArrays20 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new SimpleArrays20().execute();
    }


// file Arrays line 279
 static  class Example{
 def example(a:Array_2[Long]) {
 var sum:long = 0;
// A classic C-style for loop
for (var i:long=0; i<a.numElems_1; i++) {
  for (var j:long=0; j<a.numElems_2; j++) {
    sum += a(i,j);
  }
}

// Iterate over the indices of a using Point destructuring
for ([i,j] in a.indices()) {
    sum += a(i,j);
}

// Directly iterate over the values of a
for (v in a) {
    sum += v;
}
 } }

 static class Hook {
   def run():Boolean = true;
}

}
