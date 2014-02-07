/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;



/**
 * @author bdlucas 8/2008
 */

public class GenericOverriding03 extends GenericTest {

    class X[T] {}

    class A[T] {
        def m(X[T]) = 0;
    }

    class B[T] {
        def m(X[T]) = 1;
    }

    public def run(): boolean = {

        val x = new X[long]();
        val a = new A[long]();
        val b = new B[long]();

        genericCheck("a.m(x)", a.m(x), 0);
        genericCheck("b.m(x)", b.m(x), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericOverriding03().execute();
    }
}
