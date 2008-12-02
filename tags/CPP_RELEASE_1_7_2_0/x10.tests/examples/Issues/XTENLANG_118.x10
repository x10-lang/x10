// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_118 extends x10Test {

    class R(rank:int) { def this(rank:int) = property(rank); }

    class Bug {

        public static type R(rank:int) = R{self.rank==rank};

        class AL[T] { incomplete def toArray(): Rail[T]; }
        
        class PRL(rank:int) extends AL[R(rank)] {
            def this(rank:int) { super(); property(rank); }
        }

        class UR extends R {
            def this(rs: PRL) { super(rs.rank); val regions: Rail[R(rank)] = rs.toArray(); }
        }
    }    

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_118().execute();
    }
}
