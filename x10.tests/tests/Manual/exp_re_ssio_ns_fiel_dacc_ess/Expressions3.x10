/* Current test harness gets confused by packages, but it would be in package exp_re_ssio_ns_fiel_dacc_ess;
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

// file Expressions line 134
class Uncle {
  public static val f = 1;
}
class Parent {
  public val f = 2;
}
class Ego extends Parent {
  public val f = 3;
  class Child extends Ego {
     public val f = 4;
     def expDotId() = this.f; // 4
     def superDotId() = super.f; // 3
     def classNameDotId() = Uncle.f; // 1;
     def cnDotSuperDotId() = Ego.super.f; // 2
  }
}

class Hook {
   def run():Boolean = true;
}


public class Expressions3 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Expressions3().execute();
    }
}    
