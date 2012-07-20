/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

import harness.x10Test;

// MANAGED_X10_ONLY

public class JavaException8 extends x10Test {

    static class C[T] {T <: java.lang.Exception} {
        def f() throws T {}
        static def g[U]() {U <: java.lang.Error} throws U {} 
    }

    public def run(): Boolean = true;

    public static def main(args: Array[String](1)) {
        new JavaException8().execute();
    }

}
