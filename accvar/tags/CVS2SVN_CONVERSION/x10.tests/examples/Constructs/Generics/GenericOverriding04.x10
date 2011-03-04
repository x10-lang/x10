// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericOverriding04 extends GenericTest {

    class A[T] {
        def m() = 0;
        def m(T) = 1;
        def m(int,T) = 2;
    }

    class B[T] {
        def m() = 3;
        def m(T) = 4;
        def m(int,T) = 5;
    }

    val a = new A[int]();
    val b = new B[int]();

    public def run(): boolean = {

        check("a.m()", a.m(), 0);
        check("a.m(0)", a.m(0), 1);
        check("a.m(0,0)", a.m(0,0), 2);
        check("b.m()", b.m(), 3);
        check("b.m(0)", b.m(0), 4);
        check("b.m(0,0)", b.m(0,0), 5);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericOverriding04().execute();
    }
}
