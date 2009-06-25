// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericOverriding03 extends GenericTest {

    class X[T] {}

    class A[T] {
        def m(X[T]) = 0;
    }

    class B[T] {
        def m(X[T]) = 1;
    }

    public def run(): boolean = {

        val x = new X[int]();
        val a = new A[int]();
        val b = new B[int]();

        check("a.m(x)", a.m(x), 0);
        check("b.m(x)", b.m(x), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericOverriding03().execute();
    }
}
