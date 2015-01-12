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
 * Take a cross product of a 1-d rectange with a 2-d triangular
 * region, forming an extrusion, construct an array from it, and
 * examine it.
 */
class PolyProduct1 extends TestRegion {

    public def run() {
        var r1: Region = Region.makeRectangular(1, 2);
        var r2: Region = Region.makeLowerTriangular(3);
        var r3: Region = r1.product(r2);
        prArray("extrusion", r3);
        return status();
    }

    def expected() =
        "--- PolyProduct1: extrusion\n"+
        "rank 3\n"+
        "rect false\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 12\n"+
        "region: (x0>=1 && x1-x2>=0 && x2>=0 && x1<=2 && x0<=2)\n"+
        "  iterator\n"+
        "    --- 1\n"+
        "    0  0 . . . . . . . . . \n"+
        "    1  0 1 . . . . . . . . \n"+
        "    2  0 2 4 . . . . . . . \n"+
        "    --- 2\n"+
        "    0  0 . . . . . . . . . \n"+
        "    1  0 2 . . . . . . . . \n"+
        "    2  0 4 8 . . . . . . . \n";
    
    public static def main(Rail[String]) {
        new PolyProduct1().execute();
    }
}
