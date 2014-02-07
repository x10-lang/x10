/* Current test harness gets confused by packages, but it would be in package Arrays_Distarrays_basic_example;
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

public class Arrays360 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Arrays360().execute();
    }


// file Arrays line 843
 static  class Example {
 def example() {
val R <: Region = Region.make(1..1000);
val D <: Dist = Dist.makeBlock(R);
val da <: DistArray[Float]
       = DistArray.make[Float](D, (Point(1))=>0.0f);
}}

 static class Hook {
   def run():Boolean = true;
}

}
