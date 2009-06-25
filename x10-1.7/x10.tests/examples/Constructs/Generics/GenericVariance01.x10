// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericVariance01 extends GenericTest {

    class X {}
    class Y extends X {}
    class Z extends Y {}

    class A[T] {}

    public def run() = {

        val ay:Object = new A[Y]();
        check("ay instanceof A[X]", ay instanceof A[X], false);
        check("ay instanceof A[Y]", ay instanceof A[Y], true);
        check("ay instanceof A[Z]", ay instanceof A[Z], false);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericVariance01().execute();
    }
}
