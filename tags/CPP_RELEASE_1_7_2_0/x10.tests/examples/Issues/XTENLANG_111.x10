// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_111 extends x10Test {

    static class P {
        incomplete public static def $convert(r: ValRail[int]): P;
    }
    
    static class A {
        incomplete def set(e:int, p:P): void;
    }
        
    def foo(a:A) {
        a([1,2,3]) = 0;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_111().execute();
    }
}
