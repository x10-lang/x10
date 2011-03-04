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

/**
 * Checking that constructor calls for structs work.
 */
struct S1 {
  val x:int;
  val y:int;

  public def this(a:int, b:int) { x = a; y = b; }
  public final def sum() = x + y;
}

public class StructCall extends x10Test  {
    
    public def run():boolean {
      val a = S1(3,4);
      return a.sum() == 7;
    }

    public static def main(var args: Array[String](1)): void = {
        new StructCall().execute();
    }
}
