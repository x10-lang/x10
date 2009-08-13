// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_1 extends x10Test {

    public class C[T] {
    
        private class It implements Iterator[T] {
            incomplete public def hasNext(): boolean;
            incomplete public def next(): T;
            incomplete public def remove(): void;
        }
    
        public def iterator(): Iterator[T] {
            return new It();
        }
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_1().execute();
    }
}
