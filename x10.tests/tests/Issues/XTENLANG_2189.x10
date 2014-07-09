/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

/**
 * Checks for improper optimization of transient val fields
 * across at boundaries
 */
class XTENLANG_2189 extends x10Test {
    val a:int = 1n;
    transient val b:int = 2n;

    public def run(): boolean {
      chk(a == 1n);
      chk(b == 2n);
      at (Place.places().next(here)) {
          chk(a == 1n);
          chk(b == 0n);
      }
      return runS(this);
    }

    static def runS(t:XTENLANG_2189): boolean {
      chk(t.a == 1n);
      chk(t.b == 2n);
      at (Place.places().next(here)) {
          chk(t.a == 1n);
          chk(t.b == 0n);
      }
      return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_2189().execute();
    }
}
