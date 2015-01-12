/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers

public class InterfaceMethodCollision_MustFailCompile extends x10Test {

  public def run():boolean = true;

  static interface IDoom {
    def doom():void;
  }

  static interface IBloom {
    def doom():Int;
  }

  static @ERR interface IGloom extends IDoom, IBloom {} 

  public static def main(Rail[String]) {
    new InterfaceMethodCollision_MustFailCompile().execute();
  }
}
