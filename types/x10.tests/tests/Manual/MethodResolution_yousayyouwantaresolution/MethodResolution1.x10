/* Current test harness gets confused by packages, but it would be in package MethodResolution_yousayyouwantaresolution;
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

// file MethodResolution line 14
class Resolve {
  static def zap(Object) = "Object";
  static def zap(Resolve) = "Resolve";
  public static def main(argv:Array[String](1)) {
    Console.OUT.println(zap(1..4));
    Console.OUT.println(zap(new Resolve()));
  }
}

class Hook {
   def run():Boolean = true;
}


public class MethodResolution1 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new MethodResolution1().execute();
    }
}    
