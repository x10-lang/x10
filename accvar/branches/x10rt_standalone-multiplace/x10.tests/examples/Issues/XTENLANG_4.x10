// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_4 extends x10Test {

    class R(rank:int) {
        def this(r:int) { property(r); }
        incomplete def m(val r: int): R{self.rank==r};
    }
    
    class B extends R {
        def this(r:int) { super(r); }
        incomplete def m(val r: int): R{self.rank==r};
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_4().execute();
    }
}
