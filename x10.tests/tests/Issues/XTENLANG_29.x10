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

class XTENLANG_29 extends x10Test {

    interface M[K,V] extends (K)=>V {}
    
    class P {}
    
    class R {}
    
    class BD implements M[P,R] {
        public def m(): M[P,R] = this;
        public operator this(P): R { throw new Exception(); }
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_29().execute();
    }
}
