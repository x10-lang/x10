/* Current test harness gets confused by packages, but it would be in package Classes7a5j_Bad43_MustFailCompile;
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



public class Classes7a5j_Bad43_MustFailCompile extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes7a5j_Bad43_MustFailCompile().execute();
    }


// file Classes line 951
 // If example4() compiles, then the limitation in Classes7a5j_Bad43_MustFailCompile's section is
 // gone, so delete the whole subsection from the spec.
 static class Cls {
  property concrete(a:Long) = 7;
}
 static interface Inf {
  property nullary(): Long;
  property topLevel(z:Long):Boolean;
  property allThree(z:Long):Long;
}
 static class Example{
  def example1(Cls{self.concrete(3)==7}) = 1;
  def example2(Inf{self.nullary()==7})   = 2;
  def example3(Inf{self.topLevel(3)})    = 3;
 def example4(Inf{self.allThree(3)==7}) = "fails"; // ERR
}

 static class Hook {
   def run():Boolean = true;
}

}
