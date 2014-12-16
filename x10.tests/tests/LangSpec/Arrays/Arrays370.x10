/* Current test harness gets confused by packages, but it would be in package Arrays_DistArray_Construction_FeralWolf;
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

 import x10.regionarray.*;

public class Arrays370 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Arrays370().execute();
    }


// file Arrays line 885
 static  class Example {
 def example() {
val ident = ([i]:Point(1)) => i;
val data : DistArray[Long]
    = DistArray.make[Long](Dist.makeConstant(Region.make(1, 9)), ident);
val blk = Dist.makeBlock(Region.make(1..9, 1..9));
val data2 : DistArray[Long]
    = DistArray.make[Long](blk, ([i,j]:Point(2)) => i*j);
 }  }

 static class Hook {
   def run():Boolean = true;
}

}
