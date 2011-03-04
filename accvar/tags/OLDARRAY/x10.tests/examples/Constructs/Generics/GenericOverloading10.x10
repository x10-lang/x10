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

public class GenericOverloading10 extends GenericTest {

    static def m[T](T) = 0;
    static def m[T,U](T) = 1;

    public def run(): boolean = {

        check("m[int]()", m[int](0), 0);
        check("m[int,String](int)", m[int,String](0), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericOverloading10().execute();
    }
}
