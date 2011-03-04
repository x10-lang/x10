// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericMethods4_MustFailCompile extends GenericTest {

    def m[T](T) = {};

    public def run() = {

        m[int]("1");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericMethods4_MustFailCompile().execute();
    }
}
