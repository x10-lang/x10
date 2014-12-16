
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
 * Construct an array from a native rail and examine it.
 */
class PolyFromRail1 extends TestRegion {

    public def run() {
        val a = new Array[Double](5, [5.0,4.0,3.0,2.0,1.0]);
        prArray("from native rail", a);
        return status();
    }

    def expected() =
        "--- PolyFromRail1: from native rail\n"+
        "rank 1\n"+
        "rect true\n"+
        "zeroBased true\n"+
        "rail true\n"+
        "isConvex() true\n"+
        "size() 5\n"+
        "region: [0..4]\n"+
        "  iterator\n"+
        "5 4 3 2 1 . . . . . \n";
    
    public static def main(Rail[String]) {
        new PolyFromRail1().execute();
    }
}
