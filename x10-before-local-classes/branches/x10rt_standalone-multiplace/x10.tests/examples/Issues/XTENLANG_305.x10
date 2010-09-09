// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_305 extends x10Test {

    class A[T] implements (nat,nat,nat)=>T {
        public def set(v:T, i:int, j:int, k:int): T = 0 as T;
        public def apply(i:int, j:int, k:int): T = 0 as T;
    }

    def foo(a:A[double]!) {
        a(0,0,0)++;
    }

    public def run(): boolean {
        foo(new A[double]());
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_305().execute();
    }
}
