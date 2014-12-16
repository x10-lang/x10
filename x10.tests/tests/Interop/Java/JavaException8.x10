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

// MANAGED_X10_ONLY
// SKIP_MANAGED_X10 :  XTENLANG-3086: Implementation limitation of Managed X10.

public class JavaException8 extends x10Test {

    static class C[T] {T <: CheckedThrowable} {
        def f() throws T {}
        static def g[U]() {U <: Error} throws U {} 
    }

    public def run(): Boolean = true;

    public static def main(args: Rail[String]) {
        new JavaException8().execute();
    }

}
