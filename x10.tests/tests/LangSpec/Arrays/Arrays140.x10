/* Current test harness gets confused by packages, but it would be in package Arrays_Arrays_Arrays_Example;
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

import x10.regionarray.*;

public class Arrays140 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Arrays140().execute();
    }


// file Arrays line 541
 static  class Example{
static def addInto(src: Array[Long], dest:Array[Long])
  {src.region == dest.region}
  = {
    for (p in src.region)
       dest(p) += src(p);
  }
}
 static  class Hook{
   def run() {
     val a = new Array[Long](3, [1,2,3]);
     val b = new Array[Long](a.region, (p:Point(1)) => 10*a(p) );
     Example.addInto(a, b);
     return b(0) == 11 && b(1) == 22 && b(2) == 33;
 }}

}
