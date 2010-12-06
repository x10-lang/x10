/* Current test harness gets confused by packages, but it would be in package Classes_Innerclasses_StupidOverloading;
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

// file InnerClasses line 139
class Outer {
  def doit() {}
  def doit(String) {}
  class Inner {
     def doit(Boolean, Outer) {}
     def example() {
        doit(true, Outer.this);
        Outer.this.doit();
        //ERROR: doit("fails");
     }
  }
}

class Hook {
   def run():Boolean = true;
}


public class InnerClasses4 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new InnerClasses4().execute();
    }
}    
