/* Current test harness gets confused by packages, but it would be in package DefiniteAssignment3x6i_Bad34_MustFailCompile;
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



public class DefiniteAssignment3x6i_Bad34_MustFailCompile extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new DefiniteAssignment3x6i_Bad34_MustFailCompile().execute();
    }


// file DefiniteAssignment line 106
 static  class Example{
def example(flag:Boolean) {
  var x : Long;
  if (flag) x = 1;
  if (!flag) x = 2;
 assert x < 3; // ERR
}
}

 static class Hook {
   def run():Boolean = true;
}

}
