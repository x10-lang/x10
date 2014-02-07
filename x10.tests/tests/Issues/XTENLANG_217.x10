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
 * @author bdlucas 12/2008
 */

class XTENLANG_217 extends x10Test {

    static class A {
        operator this(i:int): double {throw new Exception();} // FIXME: XTENLANG-1443
        operator this(i:int)=(v:double) {}
    }
    
    val a = new A();
    
    def foo() {
        a(0n) = 0.0;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_217().execute();
    }
}
