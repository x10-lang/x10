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

public class GenericOverloading03_MustFailCompile extends GenericTest {

    class X[T] {}

    class A[T,U] {
        def m(X[T]) = 0;
        def m(X[U]) = 1;
    }

    public def run(): boolean = true;

    public static def main(var args: Rail[String]): void = {
        new GenericOverloading03_MustFailCompile().execute();
    }
}
