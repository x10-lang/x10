// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

//LIMITATION: overloading is by erasure

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * Two or more methods of a class or interface may have the same name
 * if they have a different number of type parameters, or they have
 * value parameters of different types.
 *
 * @author bdlucas 8/2008
 */

public class GenericOverloading05 extends GenericTest {

    def m(String) = 0;
    def m(int) = 1;
    def m(Rail[String]) = 2;
    def m(Rail[int]) = 3;

    public def run(): boolean = {

        check("m(\"1\")", m("1"), 0);
        check("m(1)", m(1), 1);
        check("m([\"0\"])", m(["0"]), 2);
        check("m([0])", m([0]), 3);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericOverloading05().execute();
    }
}
