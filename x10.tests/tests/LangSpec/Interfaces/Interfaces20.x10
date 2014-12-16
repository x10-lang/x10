/* Current test harness gets confused by packages, but it would be in package Interface_Field_Two;
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



public class Interfaces20 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Interfaces20().execute();
    }


// file Interfaces line 235
 static  interface KnowsPi {PI = 3.14159265358;}
 static class Circle implements KnowsPi {
  static def area(r:Double) = PI * r * r;
}
 static class UsesPi {
  def circumf(r:Double) = 2 * r * KnowsPi.PI;
}

 static class Hook {
   def run():Boolean = true;
}

}
