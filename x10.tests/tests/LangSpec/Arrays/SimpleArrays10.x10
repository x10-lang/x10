/* Current test harness gets confused by packages, but it would be in package Arrays_SimpleArrays_Example1;
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

 import x10.array.*;

public class SimpleArrays10 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new SimpleArrays10().execute();
    }


// file Arrays line 253
 static  class Example{
 def example(N:long) {
val a = new Array_2[double](N, N, (i:long,j:long)=>(i*j) as double);
val b = new Array_2[double](N, N, (i:long,j:long)=>(i-j) as double);
val c = new Array_2[double](N, N, (i:long,j:long)=>(i+j) as double);

for (i in 0..(N-1))
  for (j in 0..(N-1))
    for (k in 0..(N-1))
      a(i,j) += b(i,k)*c(k,j);
 } }

 static class Hook {
   def run():Boolean = true;
}

}
