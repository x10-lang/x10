// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericOverriding01 extends GenericTest {

    class A[T] {
        def m(T): int = 0;
    }
        
    class B[T] extends A[T] {
        def m(T): int = 1;
    }

    public def run(): boolean = {

        val a = new A[int]();
        val b = new B[int]();

        check("a.m(0)", a.m(0), 0);
        check("b.m(0)", b.m(0), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericOverriding01().execute();
    }
}
