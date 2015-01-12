/* Current test harness gets confused by packages, but it would be in package Types_For_Gripes_About_Pipes_Full_Of_Wipes;
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



public class Types_uniq extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types_uniq().execute();
    }


// file Types line 2333
 static   class A {} static  class B extends A{} static  class C extends A{}
 static  class D {
static def pick(t:Boolean, b:B, c:C) = t ? b : c;
}

 static class Hook {
   def run():Boolean = true;
}

}
