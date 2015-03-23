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

import harness.x10Test;
import x10.regionarray.*;

/**
 * Minimal test for regions.
 */

public class RegionTest1 extends x10Test {

    public def run(): boolean {

        var r: Region(1){rect} = Region.make(0, 100);
        var reg: Region(2){rect} = r*r;

        var sum: long = 0;
        for (p[i,j] in reg) sum += i-j;

        return sum == 0;
    }

    public static def main(var args: Rail[String]): void {
        new RegionTest1().execute();
    }
}
