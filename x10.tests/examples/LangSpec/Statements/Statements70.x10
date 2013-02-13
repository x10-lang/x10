/* Current test harness gets confused by packages, but it would be in package Statement_Case;
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



public class Statements70 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Statements70().execute();
    }


// file Statements line 509
 static  class Example {
 def example(i : Int, println: (String)=>void) {
switch (i) {
  case 1: println("one, and ");
  case 2: println("two");
          break;
  case 3: println("three");
          break;
  default: println("Something else");
           break;
}
 } }

 static class Hook {
   def run():Boolean = true;
}

}
