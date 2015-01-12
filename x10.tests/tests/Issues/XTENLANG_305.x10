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

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_305 extends x10Test {

    class A[T] implements (long,long,long)=>T {
        public operator this(i:long, j:long, k:long)=(v:T): T = 0 as T; // ERR: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
        public operator this(i:long, j:long, k:long): T = 0 as T; // ERR: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
    }

    def foo(a:A[double]) {
        a(0,0,0)++;
    }

    public def run(): boolean {
        foo(new A[double]());
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_305().execute();
    }
}
