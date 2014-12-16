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

public class GenericInheritance04 extends GenericTest {

    interface I[T] {
        def m(T):long;
    }

    class A[T] implements I[T] {
        public def m(T) = 0;
    }

    public def run() = {

        val a = new A[long]();
        genericCheck("a.m(0)", a.m(0), 0);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericInheritance04().execute();
    }
}
