/* Current test harness gets confused by packages, but it would be in package Types_Entailment_EntailFail;
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

// file Types line 1023
class Listlike(x:Int) {
  val kid : Listlike{self.x == this.x};
  def this(x:Int, kid:Listlike) {
     property(x);
     this.kid = kid as Listlike{self.x == this.x};}
}

class Hook {
   def run():Boolean = true;
}


public class Types33 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Types33().execute();
    }
}    
