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
 * Take an intersection of two rectangular regions, construct an array
 * from it, and examine it.
 */
class PolyIntersection2 extends TestRegion {

    public def run() {
        val r1 = Region.makeRectangular(1..5, 2..7);
        val r2 = Region.makeRectangular(3..8, 3..9);
        val r3 = r1.intersection(r2);
        prArray("rectangular intersection", r3);
        return status();
    }

    def expected() =
        "--- PolyIntersection2: rectangular intersection\n"+
        "rank 2\n"+
        "rect true\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 15\n"+
        "region: [3..5,3..7]\n"+
        "  iterator\n"+
        "    3  . . . 9 2 5 8 1 . . \n"+
        "    4  . . . 2 6 0 4 8 . . \n"+
        "    5  . . . 5 0 5 0 5 . . \n";
    
    public static def main(Rail[String]) {
        new PolyIntersection2().execute();
    }
}
