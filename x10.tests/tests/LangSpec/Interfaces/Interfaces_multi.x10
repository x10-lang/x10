/* Current test harness gets confused by packages, but it would be in package Interfaces_Mult_Inher_Field;
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



public class Interfaces_multi extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Interfaces_multi().execute();
    }


// file Interfaces line 272
 static interface I1 { static val a = 1;}
 static interface I2 extends I1 {}
 static interface I3 extends I1 {}
 static interface I4 extends I2,I3 {}
 static class Example implements I4 {
  def example() = a;
}

 static class Hook {
   def run():Boolean = true;
}

}
