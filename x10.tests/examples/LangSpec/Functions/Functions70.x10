/* Current test harness gets confused by packages, but it would be in package Functions_scoping_rules;
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



public class Functions70 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Rail[String]): void = {
        new Functions70().execute();
    }


// file Functions line 376
 static class Lambda {
   var a : Int = 0;
   val b = 0;
   def m(var c : Int, val d : Int) {
      var e : Int = 0;
      val f : Int = 0;
      val closure = (var i: Int, val j: Int) => {
    	  return a + b + d + f + i
               + j + this.a + Lambda.this.a;
          // c and e are not usable here
      };
      return closure;
   }
}

 static class Hook {
   def run():Boolean = true;
}

}
