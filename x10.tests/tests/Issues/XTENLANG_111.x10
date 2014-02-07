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

class XTENLANG_111 extends x10Test {

    static class P {
        public static operator (r: Rail[int]): P { throw new Exception(); }
    }
    
    static class A {
        operator this(p:P): int { throw new Exception(); } // FIXME: XTENLANG-1443
        operator this(p:P)=(e:int): void { throw new Exception(); }
    }
        
    def foo(a:A) {
        a([1n,2n,3n]) = 0n;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_111().execute();
    }
}
