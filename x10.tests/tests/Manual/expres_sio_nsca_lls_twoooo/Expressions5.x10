/* Current test harness gets confused by packages, but it would be in package expres_sio_nsca_lls_twoooo;
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

// file Expressions line 222
 class Callsome {static val closure = () => 1; static def method () = 2; static val methodEvaluated = Callsome.method();
  static def closure () = 3;
  // ERROR: static errory = Callsome.closure();
 }

class Hook {
   def run():Boolean = true;
}


public class Expressions5 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Expressions5().execute();
    }
}    
