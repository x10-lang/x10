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

import harness.x10Test;
import x10.regionarray.Array;
import x10.regionarray.Region;

/**
 * Test get and set of elements of 3D Array.
 */
public class ArrayAccess3D extends x10Test {

    public def run(): boolean = {
        val r = Region.make(0..9, 0..9, 0..9);

        val ia = new Array[Long](r, (Point)=>0);

        for ([i,j,k] in r) {
            chk(ia(i, j, k) == 0);
            ia(i, j, k) = 100*i + 10*j + k;
        }

        for ([i,j,k] in r) {
            chk(ia(i, j, k) == 100*i + 10*j + k);
        }

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayAccess3D().execute();
    }
}
