/* Current test harness gets confused by packages, but it would be in package Activities4c2r;
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



public class Activities4c2r extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Activities4c2r().execute();
    }


// file Activities line 680
 static  class Example {
 def example() {
var n : Long = 0;
finish {
  async atomic n = n + 1; //(a)
  async atomic n = n + 2; //(b)
}
}}

 static class Hook {
   def run():Boolean = true;
}

}
