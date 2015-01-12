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

/**
 * Create an unbounded region (rectangle missing a side), examine it,
 * observe UnboundedRegionException when attempting to scan it
 */

class PolyHalfspace3 extends TestRegion {

    public def run() {

        val ROW = X(0n);
        val COL = X(1n);

        val r = 
            reg(2, ROW, GE, 0n) &&
            reg(2, ROW, LE, 3n) &&
            reg(2, COL, LE, 1n);
        prUnbounded("unbounded rectangle", r);

        return status();
    }

    def expected() =
        "--- PolyHalfspace3: unbounded rectangle\n"+
        "rank 2\n"+
        "rect true\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() axis 1 has no minimum\n"+
        "region: (x0>=0 && x1<=1 && x0<=3)\n"+
        "x10.regionarray.UnboundedRegionException: axis 1 has no minimum\n";
    
    public static def main(Rail[String]) {
        new PolyHalfspace3().execute();
    }

}
