// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_217 extends x10Test {

    class A {
        def set(v:double, i:int) {}
    }
    
    val a = new A();
    
    def foo() {
        a(0) = 0.0;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_217().execute();
    }
}
