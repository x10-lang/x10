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

import x10.regionarray.*;

/**
 * Construct an upper triangular region, make an array from it, and
 * examine it.
 */
class PolyUpperTriangular1 extends TestRegion {

    public def run() {
        var r: Region = Region.makeUpperTriangular(5);
        prArray("makeUpperTriangular(5)", r);
        return status();
    }

    def expected() =
        "--- PolyUpperTriangular1: makeUpperTriangular(5)\n"+
        "rank 2\n"+
        "rect false\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 15\n"+
        "region: (x0>=0 && x1<=4 && x0-x1<=0)\n"+
        "  iterator\n"+
        "    0  0 0 0 0 0 . . . . . \n"+
        "    1  . 1 2 3 4 . . . . . \n"+
        "    2  . . 4 6 8 . . . . . \n"+
        "    3  . . . 9 2 . . . . . \n"+
        "    4  . . . . 6 . . . . . \n";
    
    public static def main(Rail[String]) {
        new PolyUpperTriangular1().execute();
    }
}
