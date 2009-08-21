// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericCast09 extends GenericTest {

    interface I[T] {
        def m(T):int;
    }

    class A implements I[int] {
        public def m(int) = 0;
    }

    public def run() = {

        var a:Object = new A();

        try {
            var i:I[String] = a as I[String];
        } catch (ClassCastException) {
            return true;
        }

        return false;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericCast09().execute();
    }
}
