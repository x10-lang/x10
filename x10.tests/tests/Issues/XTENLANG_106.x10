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
 * @author bdlucas 10/2008
 */

class XTENLANG_106 extends x10Test {

    class A[T] {
        public operator this(i0:int):T {throw new Exception();} // FIXME: XTENLANG-1443
        public operator this(i0:int)=(v:T) {}
    }
    
    def foo(a:A[double]) {
        a(0n) = 0n;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_106().execute();
    }
}
