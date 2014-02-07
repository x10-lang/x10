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
 * Testing the standard region iterator.
 */

public class RegionTestIterator extends x10Test {

    public def run(): boolean = {

        val r = Region.make(0, 100);
        val reg = r*r;

        var sum:long = 0;
        var numPts:long = 0;
        for ([i,j]:Point in reg) {
            sum += i - j;
            numPts++;
        }

        return sum == 0 && numPts == (101*101);
    }

    public static def main(var args: Rail[String]): void = {
        new RegionTestIterator().execute();
    }
}
