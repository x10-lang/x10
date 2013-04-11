/* Current test harness gets confused by packages, but it would be in package Arrays_map_reusing_space;
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
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

import x10.array.*;

public class Arrays480 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Arrays480().execute();
    }


// file Arrays line 750
 static class Example{
static def example() {
val A = new Array[Int](11, (i:long)=>i as Int);
assert A(3) == 3 && A(4) == 4 && A(10) == 10;
val cube = (i:Int) => i*i*i;
A.map(A, cube);
assert A(3) == 27 && A(4) == 64 && A(10) == 1000;
} }
 static  class Hook{ def run() {Example.example(); return true;}}

}
