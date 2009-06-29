// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericMethods1 extends GenericTest {

    def m[T](t:T) = t;

    public def run() = {

        check("m[int](1)", m[int](1), 1);
        check("m[String](\"1\")", m[String]("1"), "1");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericMethods1().execute();
    }
}
