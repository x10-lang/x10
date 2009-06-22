// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

//LIMITATION: Java translation can't handle this.

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericInstanceof06 extends GenericTest {

    interface I[T] {
        def m(T):int;
    }

    interface J[T] {
        def m(T):int;
    }

    class A implements I[int], J[String] {
        public def m(int) = 0;
        public def m(String) = 1;
    }

    public def run() = {
        
        var a:Object = new A();

        return a instanceof I[int] && a instanceof J[String];
    }

    public static def main(var args: Rail[String]): void = {
        new GenericInstanceof06().execute();
    }
}
