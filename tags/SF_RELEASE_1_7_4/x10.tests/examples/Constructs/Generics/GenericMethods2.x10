// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericMethods2 extends GenericTest {

    public def run() = {

        class A {
            def m[T](t:T) = t;
        }

        val a = new A();

        check("a.m[int](1)", a.m[int](1), 1);
        check("a.m[String](\"1\")", a.m[String]("1"), "1");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericMethods2().execute();
    }
}
