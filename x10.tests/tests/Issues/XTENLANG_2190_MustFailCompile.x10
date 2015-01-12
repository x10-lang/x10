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

/**
 * Checks for improper optimization of val fields
 * that are set both in initializers and in constructors
 */
class XTENLANG_2190_MustFailCompile extends x10Test {
    val a:long = 1;
    val b:long = 2;

    def this() {}
    @ERR @ERR def this(x:int) { a = b = x; } // Final field 'b' might already have been initialized. Final field 'a' might already have been initialized.

    public def run(): boolean {
      chk(a == 10);
      chk(b == 10);
      return runS(this);
    }

    static def runS(t:XTENLANG_2190_MustFailCompile): boolean {
      chk(t.a == 10);
      chk(t.b == 10);
      return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_2190_MustFailCompile(10).execute();
    }
}
