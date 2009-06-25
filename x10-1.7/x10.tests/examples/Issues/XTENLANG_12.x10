// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

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
            incomplete public def next(): T;
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
