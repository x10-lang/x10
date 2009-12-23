// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

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
        incomplete public def apply(P): R;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_29().execute();
    }
}
