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
import x10.regionarray.*;

/**
 *
 * Submitted by Doug Lovell.
 * @author igor 1/2006
 * @author vj 09/2008 -- Apparently the compiler cant deal with a(i)(j) = e.
 */
public class MultiDimensionalJavaArray extends x10Test {

    static PI = Math.PI;

    public def run(): boolean = {
        val MIN = 0..99;
        val MAJ = 0..9;
        val a = new Array[Array[Double](1)](Region.make(MIN), (Point) => new Array[Double](Region.make(MAJ)));

        for (val [i,j]: Point(2) in Region.make(MIN)*Region.make(MAJ))
            a(i)(j) = (i * j / PI);

	val i = MIN.max/2;
        val d = a(i);
        for (val j in MAJ) {
            chk(precision.is_equal(d(j), i * j / PI));
        }

        return true;
    }

    public static def main(Rail[String])  {
        new MultiDimensionalJavaArray().execute();
    }
}
