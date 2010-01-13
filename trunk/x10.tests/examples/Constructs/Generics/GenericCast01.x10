// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericCast01 extends GenericTest {

    interface I[T] {
        def m(T):int;
    }

    class A implements I[int] {
        public def m(int) = 0;
    }

    public def run() = {

        var a:Object = new A();
        var i:I[int]! = a as I[int];
        check("i.m(0)", i.m(0), 0);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericCast01().execute();
    }
}
