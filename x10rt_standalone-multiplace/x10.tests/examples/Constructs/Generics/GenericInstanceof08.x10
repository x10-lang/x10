// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

//LIMITATION: 
// Cannot extend different instantiations of the same interface

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericInstanceof08 extends GenericTest {

    interface I[T] {
        def m(T):int;
    }

    class A implements I[int], I[String] {
        public def m(int) = 0;
        public def m(String) = 1;
    }

    public def run() = {

        var a:Object = new A();

        return !(a instanceof I[float]);
    }

    public static def main(var args: Rail[String]): void = {
        new GenericInstanceof08().execute();
    }
}
