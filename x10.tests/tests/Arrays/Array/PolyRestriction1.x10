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
 * Construct a rectangular array, make an array view on it, modify it,
 * and examine the results in the original array.
 */
class PolyRestriction1 extends TestArray {

    public def run() {

        val r1 = Region.makeRectangular(0..5, 0..5);
        val a1 = prArray("whole array", r1);

        val r2 = Region.makeRectangular(1..3, 2..4);
        val a2 = a1.restriction(r2);
        prArray("restricted array", a2);

        for (x:Point(2) in a2.region)
            a2(x(0), x(1)) = 7.0;

        prArray("whole array modified", a1);

        return status();
    }

    def expected() =
        "--- TestArray: whole array\n"+
        "rank 2\n"+
        "rect true\n"+
        "zeroBased true\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 36\n"+
        "region: [0..5,0..5]\n"+
        "  iterator\n"+
        "    0  0 0 0 0 0 0 . . . . \n"+
        "    1  0 1 2 3 4 5 . . . . \n"+
        "    2  0 2 4 6 8 0 . . . . \n"+
        "    3  0 3 6 9 2 5 . . . . \n"+
        "    4  0 4 8 2 6 0 . . . . \n"+
        "    5  0 5 0 5 0 5 . . . . \n"+
        "--- TestArray: restricted array\n"+
        "rank 2\n"+
        "rect true\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 9\n"+
        "region: [1..3,2..4]\n"+
        "  iterator\n"+
        "    1  . . 2 3 4 . . . . . \n"+
        "    2  . . 4 6 8 . . . . . \n"+
        "    3  . . 6 9 2 . . . . . \n"+
        "--- TestArray: whole array modified\n"+
        "rank 2\n"+
        "rect true\n"+
        "zeroBased true\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 36\n"+
        "region: [0..5,0..5]\n"+
        "  iterator\n"+
        "    0  0 0 0 0 0 0 . . . . \n"+
        "    1  0 1 7 7 7 5 . . . . \n"+
        "    2  0 2 7 7 7 0 . . . . \n"+
        "    3  0 3 7 7 7 5 . . . . \n"+
        "    4  0 4 8 2 6 0 . . . . \n"+
        "    5  0 5 0 5 0 5 . . . . \n";
    
    public static def main(Rail[String]) {
        new PolyRestriction1().execute();
    }
}
