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

import x10.regionarray.*;

/**
 * Test triangular regions.
 *
 * (was RegionTriangular)
 */
class PolyContains2 extends TestRegion {

    public def run() {

        val u: Region = Region.makeRectangular(0..3, 0..3);
    
        var r1: Region = Region.makeUpperTriangular(4);
        prArray("makeUpperTriangular(4)", r1);

        var contains: String = "";
        var notContains: String = "";
        for (p:Point in u) {
            if (r1.contains(p)) contains = contains + p;
            else notContains = notContains + p;
        }
        pr("contains " + contains);
        pr("does not contain " + notContains);

        var r2: Region = Region.makeLowerTriangular(4);
        prArray("makeLowerTriangular(4)", r2);

        return status();
    }

    def expected() =
        "--- PolyContains2: makeUpperTriangular(4)\n"+
        "rank 2\n"+
        "rect false\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 10\n"+
        "region: (x0>=0 && x1<=3 && x0-x1<=0)\n"+
        "  iterator\n"+
        "    0  0 0 0 0 . . . . . . \n"+
        "    1  . 1 2 3 . . . . . . \n"+
        "    2  . . 4 6 . . . . . . \n"+
        "    3  . . . 9 . . . . . . \n"+
        "contains [0,0][0,1][0,2][0,3][1,1][1,2][1,3][2,2][2,3][3,3]\n"+
        "does not contain [1,0][2,0][2,1][3,0][3,1][3,2]\n"+
        "--- PolyContains2: makeLowerTriangular(4)\n"+
        "rank 2\n"+
        "rect false\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 10\n"+
        "region: (x0-x1>=0 && x1>=0 && x0<=3)\n"+
        "  iterator\n"+
        "    0  0 . . . . . . . . . \n"+
        "    1  0 1 . . . . . . . . \n"+
        "    2  0 2 4 . . . . . . . \n"+
        "    3  0 3 6 9 . . . . . . \n";
    
    public static def main(Rail[String]) {
        new PolyContains2().execute();
    }
}
