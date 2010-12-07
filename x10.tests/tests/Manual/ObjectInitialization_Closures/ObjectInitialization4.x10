/* Current test harness gets confused by packages, but it would be in package ObjectInitialization_Closures;
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

// file ObjectInitialization line 317
class C {
  val a = 3;
  val closure = () => a*10; // This is OK
  //val badClosure = () => b*10;
  val b = 4;
}

class Hook {
   def run():Boolean = true;
}


public class ObjectInitialization4 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new ObjectInitialization4().execute();
    }
}    
