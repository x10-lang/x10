/* Current test harness gets confused by packages, but it would be in package Activities_And_Protein;
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



public class Activities110 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Activities110().execute();
    }


// file Activities line 475
 static  class CASSizer{
var target:Any = null;
public atomic def CAS(old1: Any, y: Any):Boolean {
   if (target.equals(old1)) {
     target = y;
     return true;
   }
   return false;
}
}

 static class Hook {
   def run():Boolean = true;
}

}
