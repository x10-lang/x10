// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * STATIC SEMANTICS RULE: If a class C overrides a method of a class or
 * interface B, the guard of the method in B must entail the guard of the
 * method in C.
 *
 * @author bdlucas 8/2008
 */

public class GenericOverriding12 extends GenericTest {

    class A[T] {
        def m[U](): int = 0;
    }

    class B[T] extends A[T] {
        def m[U](): int = 1;
    }

    val a = new A[int]();
    val b = new B[int]();

    public def run() = {

        check("a.m[String](0)", a.m[String](), 0);
        check("b.m[String](0)", b.m[String](), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericOverriding12().execute();
    }
}
