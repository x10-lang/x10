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
 * @author bdlucas 10/2008
 */

class XTENLANG_111 extends x10Test {

    static class P {
        incomplete public static operator (r: ValRail[int]): P;
    }
    
    static class A {
        incomplete def set(e:int, p:P): void;
    }
        
    def foo(a:A!) {
        a([1,2,3]) = 0;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_111().execute();
    }
}
