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
 * Create a diamond missing a side, examine it, observe an
 * UnboundedRegionException when attempting to scan it.
 */

class PolyHalfspace2 extends TestRegion {

    public def run() {

        val ROW = X(0n);
        val COL = X(1n);

        val r =
            reg(2, COL+ROW, LE, 7n) &&
            reg(2, COL+ROW, GE, 3n) &&
            reg(2, COL-ROW, LE, 1n);
        prUnbounded("unbounded diamond", r);

        return status();
    }

    def expected() =
        "--- PolyHalfspace2: unbounded diamond\n"+
        "rank 2\n"+
        "rect false\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() axis 0 has no maximum\n"+
        "region: (x0+x1>=3 && x0-x1>=-1 && x0+x1<=7)\n"+
        "x10.regionarray.UnboundedRegionException: axis 0 has no maximum\n";
    
    public static def main(Rail[String]) {
        new PolyHalfspace2().execute();
    }

}
