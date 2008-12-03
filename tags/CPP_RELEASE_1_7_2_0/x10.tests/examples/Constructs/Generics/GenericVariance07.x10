// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericVariance07 extends GenericTest {

    class X {}
    class Y extends X {}
    class Z extends Y {}

    class A[-T] {}

    public def run() = {

        val a:Object = new A[Y]();
        check("a instanceof A[X]", a instanceof A[X], false);
        check("a instanceof A[Y]", a instanceof A[Y], true);
        check("a instanceof A[Z]", a instanceof A[Z], true);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericVariance07().execute();
    }
}
