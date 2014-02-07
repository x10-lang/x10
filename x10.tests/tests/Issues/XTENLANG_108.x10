/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_108 extends x10Test {


    static class R(rank:long) {

        property rect() = true;
        static type X(rank:long) = R{self.rank==rank};
        def this(rank:long) = property(rank);

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
