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
 * Checks for improper optimization of val fields
 * that are set both in initializers and in constructors
 */
class XTENLANG_2190 extends x10Test {
    val a:int = 1;
    val b:int = 2;

    def this() {}
    def this(x:int) { a = b = x; }

    public def run(): boolean {
      chk(a == 10);
      chk(b == 10);
      return runS(this);
    }

    static def runS(t:XTENLANG_2190): boolean {
      chk(t.a == 10);
      chk(t.b == 10);
      return true;
    }

    public static def main(Array[String](1)) {
        new XTENLANG_2190(10).execute();
    }
}
