// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_47 extends x10Test {

    class C(rank:nat) {
        def this(r:nat) = property(r);
    }
    
    class CL(rank2:nat) {
    
        private class It implements Iterator[C{self.rank==rank2}] {
            incomplete public def hasNext(): boolean;
            incomplete public def next(): C{self.rank==rank2};
            incomplete public def remove(): void;
        }
    
        def this(r:nat) = property(r);
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_47().execute();
    }
}
