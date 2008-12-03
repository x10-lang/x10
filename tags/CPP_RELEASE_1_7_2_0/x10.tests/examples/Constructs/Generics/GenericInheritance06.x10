// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericInheritance06 extends GenericTest {

    interface I[T] {
        def m(T):int;
        def n(T):int;
    }

    interface J[T] {
        def m(T):int;
        def o(T):int;
    }

    class A[T] implements I[T], J[T] {
        public def m(T) = 0;
        public def n(T) = 1;
        public def o(T) = 2;
    }

    public def run() = {

        val a = new A[int]();
        check("a.m(0)", a.m(0), 0);
        check("a.n(0)", a.n(0), 1);
        check("a.o(0)", a.o(0), 2);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericInheritance06().execute();
    }
}
