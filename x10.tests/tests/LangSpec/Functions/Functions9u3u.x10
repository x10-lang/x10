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
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;



public class Functions9u3u extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Functions9u3u().execute();
    }


// file Functions line 403

 // OK, we want to do the negative tests, but they don't work properly.
 static  class Lambda {
    var a : Long = 0;
    val b = 0;
    def m(var c : Long, val d : Long) {
       var e : Long = 0;
       val f : Long = 0;
       val closure = (var i: Long, val j: Long) => {
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
