/* Current test harness gets confused by packages, but it would be in package Functions9u3u;
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



public class Functions9u3u extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Rail[String]): void = {
        new Functions9u3u().execute();
    }


// file Functions line 400

 // OK, we want to do the negative tests, but they don't work properly.
 static  class Lambda {
    var a : Int = 0;
    val b = 0;
    def m(var c : Int, val d : Int) {
       var e : Int = 0;
       val f : Int = 0;
       val closure = (var i: Int, val j: Int) => {
     	  // return a + b + d + f + j + this.a + Lambda.this.a;
           // ERROR: return c;
           // ERROR: return e;
       };
       return closure;
    }
 }

 static class Hook {
   def run():Boolean = true;
}

}
