// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericMethods5_MustFailCompile extends GenericTest {

    def m[T,U](T,U) = {};

    public def run() = {

        m[String,int](1,"1");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericMethods5_MustFailCompile().execute();
    }
}
