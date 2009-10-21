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

public class GenericOverriding10_MustFailCompile extends GenericTest {

    class A[T] {
        def m[U](T) = 0;
    }

    class B[T] extends A[T] {
        def m[U](T){T<:U} = 1;
    }

    val a = new A[int]();
    val b = new B[int]();

    public def run() = {

        check(

    public static def main(var args: Rail[String]): void = {
        new GenericOverriding10_MustFailCompile().execute();
    }
}
