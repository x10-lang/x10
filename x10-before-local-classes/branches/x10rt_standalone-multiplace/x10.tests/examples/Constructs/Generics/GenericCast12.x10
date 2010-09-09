// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericCast12 extends GenericTest {

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

        var a:Object = new A[int]();

        var exceptions:int = 0;

        try {
            var i:I[String] = a as I[String];
        } catch (ClassCastException) {
            exceptions++;
        }

        try {
            var j:J[String] = a as J[String];
        } catch (ClassCastException) {
            exceptions++;
        }

        return exceptions==2;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericCast12().execute();
    }
}
