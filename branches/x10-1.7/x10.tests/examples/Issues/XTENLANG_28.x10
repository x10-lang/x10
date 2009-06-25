// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_28 extends x10Test {

    class P {}
    
    public class AL[T] {
    
        public def add(T) = {}
    }
    
    class PL extends AL[P] {
    
        public def add(p:P) {
            super.add(p);
        }
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_28().execute();
    }
}
