/* Current test harness gets confused by packages, but it would be in package expsome_Arrays13;
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



public class Arrays2f2y extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Arrays2f2y().execute();
    }


// file Arrays line 165

 static class Arrays13TestExp{
  def check(size: int, a: int, b: int)  = Region.makeBanded(size, a, b);  }

 static class Hook {
   def run():Boolean = true;
}

}
