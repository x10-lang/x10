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
 * Intersect a rectangular region with a full region, construct an
 * array from it, and examine it.
 */
class PolyIntersection3 extends TestRegion {

    public def run() {
        val r1 = Region.makeRectangular(1..3, 2..4);
        val r2 = Region.makeFull(2);
        val r3 = r1.intersection(r2);
        prArray("full intersected with rectangle", r3);
        return status();
    }

    def expected() =
        "--- PolyIntersection3: full intersected with rectangle\n"+
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
        "    3  . . 6 9 2 . . . . . \n";
    
    public static def main(Rail[String]) {
        new PolyIntersection3().execute();
    }
}
