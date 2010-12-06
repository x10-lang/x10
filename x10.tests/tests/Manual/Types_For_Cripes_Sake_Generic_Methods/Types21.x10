/* Current test harness gets confused by packages, but it would be in package Types_For_Cripes_Sake_Generic_Methods;
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

// file Types line 522
 import x10.util.*;
class NonGeneric {
  static def first[T](x:List[T]):T = x(0);
  def m(z:List[Int]) {
    val f = first[Int](z);
    val g = first(z);
    return f == g;
  }
}

class Hook {
   def run():Boolean = true;
}


public class Types21 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Types21().execute();
    }
}    
