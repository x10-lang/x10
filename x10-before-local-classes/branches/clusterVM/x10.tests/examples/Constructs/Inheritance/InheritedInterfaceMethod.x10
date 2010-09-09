// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * Inherited methods should satisfy interface dependences.
 * Adapted from the test case for XTENLANG-22.
 * @author igor 06/2009
 */
class InheritedInterfaceMethod extends x10Test {

    class A {
        public def m() = 0;
    }

    interface I {
        def m(): int;
    }

    class B extends A implements I {}

    public def run(): boolean {
        val v = new B();
        return v.m() == 0;
    }

    public static def main(Rail[String]) {
        new InheritedInterfaceMethod().execute();
    }
}

