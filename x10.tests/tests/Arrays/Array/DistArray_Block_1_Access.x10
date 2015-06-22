/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */

import harness.x10Test;
import x10.array.DistArray_Block_1;

/**
 * Test get and set of elements of DistArray_Block_1.
 */
public class DistArray_Block_1_Access extends x10Test {

    public def run(): boolean {
        val da = new DistArray_Block_1[Long](7, (i:Long) => 0);

        finish for (place in da.placeGroup()) at(place) {
            for ([i] in da.localIndices()) {
                chk(da(i) == 0);
                da(i) = i;
            }

            for ([i] in da.localIndices()) {
                chk(da(i) == i);
            }
        }

        return true;
    }

    public static def main(var args: Rail[String]): void {
        new DistArray_Block_1_Access().execute();
    }
}
