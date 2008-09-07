// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericOverloading11_MustFailCompile extends GenericTest {

    static def m[T,U](T) = 0;
    static def m[T,U](U) = 1;

    public def run(): boolean = true;

    public static def main(var args: Rail[String]): void = {
        new GenericOverloading11_MustFailCompile().execute();
    }
}
