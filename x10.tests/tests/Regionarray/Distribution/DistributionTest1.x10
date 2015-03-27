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
 * Minimal test for dists.
 */

public class DistributionTest1 extends x10Test {

    public def run(): boolean {
        val r= Region.make(0,100);
        val R = r*r;
        val D  = R->here;
        return ((D(0, 0) == here) &&
            (D.rank == 2) &&
            (R.rank == 2) &&
            (R.max(1) - R.min(1) + 1 == 101L));
    }

    public static def main(var args: Rail[String]): void {
        new DistributionTest1().execute();
    }
}
