// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_11 extends x10Test {

    class H {}
    
    class A[T] {
        incomplete public def add(v: T): void;
        incomplete public def iterator(): Iterator[T];
    }
    
    public def foo(hl: A[H]) {
        var it: Iterator[H] = hl.iterator();
        hl.add(it.next());
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_11().execute();
    }
}
