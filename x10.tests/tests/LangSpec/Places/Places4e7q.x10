/* Current test harness gets confused by packages, but it would be in package expsome_Places10;
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



public class Places4e7q extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Places4e7q().execute();
    }


// file Places line 975

 static class Places10TestExp{
  def check(g:GlobalRef[Any]{here == g.home})  = g();  }

 static class Hook {
   def run():Boolean = true;
}

}
