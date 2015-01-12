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
 * @author bdlucas 10/2008
 */

class XTENLANG_132_MustFailCompile extends x10Test {

    static class A(p:int) {
        def this(p:int) = property(p);
    }
    
    static class Bug {
    
        static type A(p:int) = A{self.p==p};
    
        public static def run():boolean {
    
            val a = new A(0n);
            val a1 = a as A(1n); // ERR Must fail compilation.
            return false;
        }
    }

    public def run()=Bug.run();

    public static def main(Rail[String]) {
        new XTENLANG_132_MustFailCompile().execute();
    }
}
