/* Current test harness gets confused by packages, but it would be in package Expressions3c4m;
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



public class Expressions3c4m extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Expressions3c4m().execute();
    }


// file Expressions line 1067
 static class Cont {
   operator this in (Int) = true;
   operator (String) in this = false;
   static operator (Cont) in (b:Boolean) = b;
   static def example() {
      val c:Cont = new Cont();
      assert c in 4 && !("odd" in c) && (c in true);
   }
}
 static class Hook{ def run() { Cont.example(); return true; } }

}