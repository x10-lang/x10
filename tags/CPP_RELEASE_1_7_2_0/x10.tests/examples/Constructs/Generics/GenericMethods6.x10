// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericMethods6 extends GenericTest {

    public def run() = {

        class A[U] {
            def m[T](u:U,t:T) = t;
        }

        val a1 = new A[String]();
        check("a1.m[int](\"1\",1)", a1.m[int]("1",1), 1);

        val a2 = new A[int]();
        check("a2.m[String](1,\"1\")", a2.m[String](1,"1"), "1");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericMethods6().execute();
    }
}
