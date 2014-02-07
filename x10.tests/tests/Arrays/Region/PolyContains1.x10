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
 * Test banded regions.
 *
 * (was RegionBanded)
 */
class PolyContains1 extends TestRegion {

    public def run() {

        val u: Region = Region.makeRectangular(0..4, 0..4);
    
        var b1: Region = Region.makeBanded(5, 1, 1);
        prArray("makeBanded(5,1,1)", b1);

        var contains: String = "";
        var notContains: String = "";
        for (p:Point in u) {
            if (b1.contains(p)) contains = contains + p;
            else notContains = notContains + p;
        }
        pr("contains " + contains);
        pr("does not contain " + notContains);

        var b2: Region = Region.makeBanded(5, 1, 2);
        prArray("makeBanded(5,1,2)", b2);

        var b3: Region = Region.makeBanded(5, 2, 1);
        prArray("makeBanded(5,2,1)", b3);


        return status();
    }

    def expected() =
        "--- PolyContains1: makeBanded(5,1,1)\n"+
        "rank 2\n"+
        "rect false\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 5\n"+
        "region: (x0-x1>=0 && x1>=0 && x0-x1<=0 && x0<=4)\n"+
        "  iterator\n"+
        "    0  0 . . . . . . . . . \n"+
        "    1  . 1 . . . . . . . . \n"+
        "    2  . . 4 . . . . . . . \n"+
        "    3  . . . 9 . . . . . . \n"+
        "    4  . . . . 6 . . . . . \n"+
        "contains [0,0][1,1][2,2][3,3][4,4]\n"+
        "does not contain [0,1][0,2][0,3][0,4][1,0][1,2][1,3][1,4][2,0][2,1][2,3][2,4][3,0][3,1][3,2][3,4][4,0][4,1][4,2][4,3]\n"+
        "--- PolyContains1: makeBanded(5,1,2)\n"+
        "rank 2\n"+
        "rect false\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 9\n"+
        "region: (x0-x1>=0 && x1>=0 && x0-x1<=1 && x0<=4)\n"+
        "  iterator\n"+
        "    0  0 . . . . . . . . . \n"+
        "    1  0 1 . . . . . . . . \n"+
        "    2  . 2 4 . . . . . . . \n"+
        "    3  . . 6 9 . . . . . . \n"+
        "    4  . . . 2 6 . . . . . \n"+
        "--- PolyContains1: makeBanded(5,2,1)\n"+
        "rank 2\n"+
        "rect false\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 9\n"+
        "region: (x0>=0 && x0-x1>=-1 && x1<=4 && x0-x1<=0)\n"+
        "  iterator\n"+
        "    0  0 0 . . . . . . . . \n"+
        "    1  . 1 2 . . . . . . . \n"+
        "    2  . . 4 6 . . . . . . \n"+
        "    3  . . . 9 2 . . . . . \n"+
        "    4  . . . . 6 . . . . . \n";
    
    public static def main(Rail[String]) {
        new PolyContains1().execute();
    }
}
