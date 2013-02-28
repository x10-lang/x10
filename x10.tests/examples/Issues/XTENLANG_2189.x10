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
 * Checks for improper optimization of transient val fields
 * across at boundaries
 */
class XTENLANG_2189 extends x10Test {
    val a:int = 1;
    transient val b:int = 2;

    public def run(): boolean {
      chk(a == 1);
      chk(b == 2);
      at (here.next()) {
          chk(a == 1);
          chk(b == 0);
      }
      return runS(this);
    }

    static def runS(t:XTENLANG_2189): boolean {
      chk(t.a == 1);
      chk(t.b == 2);
      at (here.next()) {
          chk(t.a == 1);
          chk(t.b == 0);
      }
      return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_2189().execute();
    }
}
