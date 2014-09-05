/* Current test harness gets confused by packages, but it would be in package Types_For_Gripes_About_Snipes;
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



public class Types460 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types460().execute();
    }


// file Types line 2366
 static interface I1 {}
 static interface I2 {}
 static class A implements I1, I2 {}
 static class B implements I1, I2 {}
 static class C {
  static def example(t:Boolean, a:A, b:B) = t ? a : b;
}

 static class Hook {
   def run():Boolean = true;
}

}
