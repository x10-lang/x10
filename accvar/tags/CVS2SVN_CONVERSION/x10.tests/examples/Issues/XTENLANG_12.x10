// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

interface I[T] {
    def next(): T;
}

class AL[T] {

    private static class It[T] implements I[T] {
        incomplete public def next(): T;
        def this(al:AL[T]) {}
    }

    public def it(): I[T] {
        return new It[T](this);
    }

}

class XTENLANG_12 extends x10Test {

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_12().execute();
    }
}
