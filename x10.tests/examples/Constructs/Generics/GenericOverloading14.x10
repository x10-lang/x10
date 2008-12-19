// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericOverloading14 extends GenericTest {

    class A[T] {
        def m() = 0;
        def m(T) = 1;
    }

    class B[S] extends A[S] {
        def m(int,S) = 2;
    }

    val  b = new  B[int]();

    public def run(): boolean = {

        check(" b.m()",  b.m(), 0);
        check(" b.m(0)",  b.m(0), 1);
        check(" b.m(0,0)",  b.m(0,0), 2);

        return result;
    }


    public static def main(var args: Rail[String]): void = {
        new GenericOverloading14().execute();
    }
}
