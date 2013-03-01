/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Testing 3D arrays.
 */

public class Array2v extends x10Test {

    public def run(): boolean = {

        val e = 0..9;
        val r = e*e*e;

        chk(r.equals((0..9)*(0..9)*(0..9)));

        val ia = new Array[int](r, (Point)=>0);

        for (val [i,j,k]: Point in r) {
            chk(ia(i, j, k) == 0);
            ia(i, j, k) = 100*i + 10*j + k;
        }

        for (val [i,j,k]: Point in r) {
            chk(ia(i, j, k) == 100*i + 10*j + k);
        }

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new Array2v().execute();
    }
}
