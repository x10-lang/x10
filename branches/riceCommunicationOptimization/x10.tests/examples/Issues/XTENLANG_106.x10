// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_106 extends x10Test {

    class A[T] {
        public def set(v:T, i0:int) {}
    }
    
    def foo(a:A[double]) {
        a(0) = 0;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_106().execute();
    }
}
