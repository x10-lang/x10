/* Current test harness gets confused by packages, but it would be in package Arrays251;
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

public class Arrays251 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Arrays251().execute();
    }


// file Arrays line 688
 static class Example{
static def allNonNegatives(a:Array[Double]):Boolean {
 for (v in a.values()) if (v < 0.0D) return false;
 return true;
}
}
 static  class Hook{ def run() { val a = new Array[Double](2, [1.0,2.0]); return Example.allNonNegatives(a);  }}

}
