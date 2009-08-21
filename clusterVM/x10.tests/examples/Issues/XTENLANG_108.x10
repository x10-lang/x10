// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_108 extends x10Test {


    static class R(rank:int) {

        property rect = true;
        static type X(rank:int) = R{self.rank==rank};
        def this(rank:int) = property(rank);

        var a: X(1){rect};
        var b: X(1) = a; // does not typecheck

        // above should be equivalent to these, which do typecheck
        var c: R{self.rank==1&&rect};
        var d: R{self.rank==1} = c;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_108().execute();
    }
}
