/* Current test harness gets confused by packages, but it would be in package Functions_areLikeGrunctions_fromConjunctionJunctions;
*/

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

// file Functions line 275
class Lambda {
   var a : Int = 0;
   val b = 0;
   def m(var c : Int, val d : Int) {
      var e : Int = 0;
      val f : Int = 0;
      val closure = (var i: Int, val j: Int) => {
    	  return a + b + d + f + j + this.a + Lambda.this.a;
          // ILLEGAL: return c + e + i;
      };
      return closure;
   }
}

class Hook {
   def run():Boolean = true;
}


public class Functions7 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Functions7().execute();
    }
}    
