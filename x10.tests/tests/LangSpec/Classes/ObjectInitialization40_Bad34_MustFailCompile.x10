/* Current test harness gets confused by packages, but it would be in package ObjectInitialization_Closures;
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



public class ObjectInitialization40_Bad34_MustFailCompile extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new ObjectInitialization40_Bad34_MustFailCompile().execute();
    }


// file Classes line 3013
 static class C {
  val a:Long;
  def this() {
    this.a = 1;
    //ERROR: val bad1 = () => this;
 val bad2 = () => a*10; // ERR
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
