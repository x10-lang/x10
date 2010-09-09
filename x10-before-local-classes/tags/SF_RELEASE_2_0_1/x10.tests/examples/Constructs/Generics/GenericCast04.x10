// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericCast04 extends GenericTest {

    interface I[T] {
        def m(T):int;
    }

    class A[T] implements I[T] {
        public def m(T) = 0;
    }

    public def run() = {

        var a:Object = new A[int]();
        var i:I[int]! = a as I[int];
        check("i.m(0)", i.m(0), 0);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericCast04().execute();
    }
}
