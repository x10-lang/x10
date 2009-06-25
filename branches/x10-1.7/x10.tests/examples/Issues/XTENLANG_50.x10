// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_50 extends x10Test {

    static class R(rank:nat) {
    
        public static def make(val rs: ValRail[R]): R{rank==rs.length} {
            return new R(rs.length) as R{rank==rs.length};
        }
    
        public static def $convert(rs: ValRail[R]) = make(rs);
    
        // workaround: declare return type explicitly
        //public static def $convert(rs: ValRail[R]): R{rank==rs.length} = make(rs);
    
        def this(rank:nat) = property(rank);
    }
    
    val x: R{rank==2} = [new R(1), new R(1)];

    // this doesn't work either, i.e. problem isn't with automatic conversion
    //val x: R{rank==2} = R.$convert([new R(1), new R(1)]);

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_50().execute();
    }
}
