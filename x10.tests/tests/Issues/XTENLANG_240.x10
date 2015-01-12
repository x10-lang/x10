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

class XTENLANG_240 extends x10Test {

    
    class A {
        final operator this(i0: int)=(v:double) {}
        final public operator this(i0:int) = 0;
    }
        
    def foo(a:A) {
        for (var i:int=0n; i<0n; i++)
            a(i) = 0.0;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_240().execute();
    }
}
