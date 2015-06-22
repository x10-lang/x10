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
 * Test a the invocation of .equals on a method type parameter.
 *
 * @author nystrom 8/2008
 */
public class GenericMethodEquals extends x10Test {

    static class A {

        public def equals(Any) {
            return true;
        }

    }

    static class B extends A {

    }

    public static def test[T] (a:T) {
        return a.equals(a);
    }

    public def run(): boolean {
        return test[B](new B());
    }

    public static def main(var args: Rail[String]): void {
        new GenericMethodEquals().execute();
    }
}

