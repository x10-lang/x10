/* Current test harness gets confused by packages, but it would be in package Types2y3i_Bad35_MustFailCompile;
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



public class Types2y3i_Bad35_MustFailCompile extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Types2y3i_Bad35_MustFailCompile().execute();
    }


// file Types line 369

 static interface Stat {
  static val PI = 3.14159;
  static type R = Double;
  static  class Pair(x:R, y:R) {}
  // ERROR: static def meth():Int;
 static def this(); // ERR
}
 static class Example {
  static def example() {
     val p : Stat.Pair = new Stat.Pair(Stat.PI, Stat.PI);
  }
}

 static class Hook {
   def run():Boolean = true;
}

}