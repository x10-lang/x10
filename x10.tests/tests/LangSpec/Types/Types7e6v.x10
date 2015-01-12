/* Current test harness gets confused by packages, but it would be in package Types7e6v;
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



public class Types7e6v extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types7e6v().execute();
    }


// file Types line 2618
 static class Numb(p:Long){
  static def dup(n:Long){n != 0} = new Numb(n);
  public static def example(n:Long) {
    val x = dup(n as Long{self != 0});
    val y : Numb{self.p==n, n!=0, self!=null} = x;
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
