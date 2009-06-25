// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_112 extends x10Test {

    static class D {}
    
    static class A[T](d:D) {
        static type A[T](d:D) = A[T]{self.d==d};
        static def make[T](d:D): A[T](d) {return null;}
        def this(d:D) = property(d);
    }
    
    static val d: D = new D();
    static val a: A[int] = A.make[int](d);

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_112().execute();
    }
}
