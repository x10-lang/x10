/* Current test harness gets confused by packages, but it would be in package classes_fields_secundus;
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

// file Classes line 183
class A {
   val f = 3;
}
class B extends A {
   val f = 4;
   class C extends B {
      // C is both a subclass and inner class of B
      val f = 5;
      def foo()
         = f          // 5
         + super.f    // 4
         + B.this.f   // 4 (the "f" of the outer instance)
         + B.super.f; // 3 (the "super.f" of the outer instance)
    }
}

class Hook {
   def run():Boolean = true;
}


public class Classes3 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Classes3().execute();
    }
}    
