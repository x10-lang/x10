// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_62 extends x10Test {

    class A[T] {
        public def set(v:T, i0: int) {}
    }
    
    def foo(a:A[double]!) {
        //a.set(0.0, 0); // this works
        a(0) = 0.0; // this doesn't
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_62().execute();
    }
}
