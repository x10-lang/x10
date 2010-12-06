/* Current test harness gets confused by packages, but it would be in package Classes_Transient_Example;
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

// file Classes line 233
class Trans {
   val copied = "copied";
   transient var transy : String = "a very long string";
   def example() {
      at (here) { // causes copying
         assert(this.copied.equals("copied"));
         assert(this.transy == null);
      }
   }
}

class Hook {
   def run():Boolean = true;
}


public class Classes4 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Classes4().execute();
    }
}    
