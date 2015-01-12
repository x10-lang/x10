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
 * Intersect a rectangular region with a triangular region, construct
 * an array from it, and examine it.
 */
class PolyIntersection1 extends TestRegion {

    public def run() {

        val r1 = Region.makeUpperTriangular(2, 1, 7);
        prArray("tri", r1);

        val r2 = Region.makeRectangular(3..6, 3..7);
        prArray("rect", r2);

        val r3 = r2.intersection(r1);
        prArray("intersection", r3);

        return status();
    }

    def expected() =
        "--- PolyIntersection1: tri\n"+
        "rank 2\n"+
        "rect false\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 28\n"+
        "region: (x0>=2 && x1<=7 && x0-x1<=1)\n"+
        "  iterator\n"+
        "    2  . 2 4 6 8 0 2 4 . . \n"+
        "    3  . . 6 9 2 5 8 1 . . \n"+
        "    4  . . . 2 6 0 4 8 . . \n"+
        "    5  . . . . 0 5 0 5 . . \n"+
        "    6  . . . . . 0 6 2 . . \n"+
        "    7  . . . . . . 2 9 . . \n"+
        "    8  . . . . . . . 6 . . \n"+
        "--- PolyIntersection1: rect\n"+
        "rank 2\n"+
        "rect true\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 20\n"+
        "region: [3..6,3..7]\n"+
        "  iterator\n"+
        "    3  . . . 9 2 5 8 1 . . \n"+
        "    4  . . . 2 6 0 4 8 . . \n"+
        "    5  . . . 5 0 5 0 5 . . \n"+
        "    6  . . . 8 4 0 6 2 . . \n"+
        "--- PolyIntersection1: intersection\n"+
        "rank 2\n"+
        "rect false\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 17\n"+
        "region: (x0>=3 && x1>=3 && x1<=7 && x0-x1<=1 && x0<=6)\n"+
        "  iterator\n"+
        "    3  . . . 9 2 5 8 1 . . \n"+
        "    4  . . . 2 6 0 4 8 . . \n"+
        "    5  . . . . 0 5 0 5 . . \n"+
        "    6  . . . . . 0 6 2 . . \n";
    
    public static def main(Rail[String]) {
        new PolyIntersection1().execute();
    }
}
