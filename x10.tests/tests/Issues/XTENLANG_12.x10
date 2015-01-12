/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_12 extends x10Test {

    static interface I[T] {
        def next(): T;
    }

    static class AL[T] {

        private static class It[T] implements I[T] {
            public def next(): T { throw new Exception(); }
            def this(al:AL[T]) {}
        }

        public def it(): I[T] {
            return new It[T](this);
        }

    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_12().execute();
    }
}
