/* Current test harness gets confused by packages, but it would be in package Activities_AtAndVariables;
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

// file Activities line 488
 abstract class Example {
 abstract def mathProcessor() : Place;
def printRootsOfQuadratic(a:Complex, b:Complex, c:Complex) {
  var r : Complex = Complex(0,0);
  var s : Complex = Complex(0,0);
  val h = here;
  at(mathProcessor()) {
    val disc = Math.sqrt(b*b - 4*a*c);
    val rr = (-b + disc) / (2*a);
    val ss = (-b - disc) / (2*a);
    at(h) {
      r = rr; s = ss;
    }
  }
  Console.OUT.println("r = " + r + "; s = " + s);
}
}

class Hook {
   def run():Boolean = true;
}


public class Activities8 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Activities8().execute();
    }
}    
