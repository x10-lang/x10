// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericInstanceof04 extends GenericTest {

    interface I[T] {
        def m(T):int;
        def n(T):int;
    }

    interface J[T] {
        def m(T):int;
        def o(T):int;
    }

    class A implements I[int], J[int] {
        public def m(int) = 0;
        public def n(int) = 1;
        public def o(int) = 2;
    }

    public def run() = {

        var a:Object = new A();

        return a instanceof I[int] && a instanceof J[int];
    }

    public static def main(var args: Rail[String]): void = {
        new GenericInstanceof04().execute();
    }
}
