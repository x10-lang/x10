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

public class GenericOverriding05 extends GenericTest {

    static class A[T] {
        def m(): long = 0;
        def m(T): long = 1;
        def m(long,T): long = 2;
    }

    static class B[T] extends A[T] {
        def m(): long = 3;
    }

    val a = new A[long]();
    val b = new B[long]();

    public def run(): boolean {

        genericCheck("a.m()", a.m(), 0);
        genericCheck("a.m(0)", a.m(0), 1);
        genericCheck("a.m(0,0)", a.m(0,0), 2);
        genericCheck("b.m()", b.m(), 3);
        genericCheck("b.m(0)", b.m(0), 1);
        genericCheck("b.m(0,0)", b.m(0,0), 2);

        return result;
    }

    public static def main(var args: Rail[String]): void {
        new GenericOverriding05().execute();
    }
}
