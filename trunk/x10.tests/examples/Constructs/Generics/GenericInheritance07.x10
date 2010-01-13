// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericInheritance07 extends GenericTest {

    interface I[T] {
        def m(T):int;
    }

    interface J[T] {
        def m(T):int;
    }

    class A[T] implements I[T], J[T] {
        public def m(T) = 0;
    }

    public def run() = {
        
        val a = new A[int]();
        val i:I[int]! = a;
        val j:J[int]! = a;

        check("a.m(0)", a.m(0), 0);
        check("i.m(0)", i.m(0), 0);
        check("j.m(0)", j.m(0), 0);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericInheritance07().execute();
    }
}
