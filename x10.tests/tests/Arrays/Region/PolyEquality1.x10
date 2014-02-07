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
 * Basic rectangular region equality.
 *
 * (was RegionEquality)
 */
class PolyEquality1 extends TestRegion {

    public def run() {

        val r1 = Region.makeRectangular(0..5, 1..6) as Region(2);
        val r2 = Region.makeRectangular(1..6, 2..7) as Region(2);
        val r3 = Region.makeRectangular(1..5, 2..6) as Region(2);
        val r4 = r1.intersection(r2);

        comp(r1,r2);
        comp(r1,r3);
        comp(r1,r4);
        comp(r3,r4);

        comp(r2,r1);
        comp(r3,r1);
        comp(r4,r1);
        comp(r4,r3);

        return status();
    }


    def comp(a: Region, b: Region{self.rank==a.rank}): void {
        pr(""+a + "==" + b + " " + a.equals(b) + " " + a.equals(b));
    }
    def expected() =
        "[0..5,1..6]==[1..6,2..7] false false\n"+
        "[0..5,1..6]==[1..5,2..6] false false\n"+
        "[0..5,1..6]==[1..5,2..6] false false\n"+
        "[1..5,2..6]==[1..5,2..6] true true\n"+
        "[1..6,2..7]==[0..5,1..6] false false\n"+
        "[1..5,2..6]==[0..5,1..6] false false\n"+
        "[1..5,2..6]==[0..5,1..6] false false\n"+
        "[1..5,2..6]==[1..5,2..6] true true\n";
    
    public static def main(Rail[String]) {
        new PolyEquality1().execute();
    }
}
