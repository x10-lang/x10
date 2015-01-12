/* Current test harness gets confused by packages, but it would be in package Types_subtyping_cons;
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



public class Types400 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types400().execute();
    }


// file Types line 2166
 
 static class ConsTy[T,U] {
   def upcast(t:T){T <: U} :U = t;
}

 static class Hook {
   def run():Boolean = true;
}

}
