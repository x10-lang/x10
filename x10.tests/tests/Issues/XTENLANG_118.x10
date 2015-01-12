/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_118 extends x10Test {

    class R(rank:int) { def this(rank:int) = property(rank); }

    class Bug {

        public static type R(rank:int) = R{self.rank==rank};

        class AL[T] { def toArray(): Rail[T] { throw new Exception(); } }
        
        class PRL(rank:int) extends AL[R(rank)] {
            def this(rank:int) { super(); property(rank); }
        }

        class UR extends R {
            def this(rs: PRL) { super(rs.rank); val regions: Rail[R(rs.rank)] = rs.toArray(); }
        }
    }    

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_118().execute();
    }
}
