// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * Two or more methods of a class or interface may have the same name
 * if they have a different number of type parameters, or they have
 * value parameters of different types.
 *
 * @author bdlucas 8/2008
 */

public class GenericOverloading06 extends GenericTest {

    class A[T] {
        def m() = 0;
        def m(T) = 1;
        def m(int,T) = 2;
    }

    val a = new A[int]();

    public def run(): boolean = {

        check("a.m()", a.m(), 0);
        check("a.m(0)", a.m(0), 1);
        check("a.m(0,0)", a.m(0,0), 2);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericOverloading06().execute();
    }
}
