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
 * Construct a 3-d rectangular region, make an array from from it, and
 * examine it.
 */
class PolyRectangular1 extends TestRegion {

    public def run() {
        var r: Region = Region.makeRectangular(1..2, 2..4, 3..6);
        prArray("3-d rect array", r);
        return status();
    }

    def expected() =
        "--- PolyRectangular1: 3-d rect array\n" + 
        "rank 3\n" + 
        "rect true\n" + 
        "zeroBased false\n" + 
        "rail false\n" + 
        "isConvex() true\n" + 
        "size() 24\n" + 
        "region: [1..2,2..4,3..6]\n" + 
        "  iterator\n" + 
        "    --- 1\n" + 
        "    2  . . . 6 8 0 2 . . . \n" + 
        "    3  . . . 9 2 5 8 . . . \n" + 
        "    4  . . . 2 6 0 4 . . . \n" + 
        "    --- 2\n" + 
        "    2  . . . 2 6 0 4 . . . \n" + 
        "    3  . . . 8 4 0 6 . . . \n" +
        "    4  . . . 4 2 0 8 . . . \n";

    public static def main(Rail[String]) {
        new PolyRectangular1().execute();
    }
}
