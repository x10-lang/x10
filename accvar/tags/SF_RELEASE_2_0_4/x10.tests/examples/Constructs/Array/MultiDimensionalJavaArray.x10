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
 *
 * Submitted by Doug Lovell.
 * @author igor 1/2006
 * @author vj 09/2008 -- Apparently the compiler cant deal with a(i)(j) = e.
 */

public class MultiDimensionalJavaArray extends x10Test {

    const PI = Math.PI;

    public def run(): boolean = {
        val MIN = 0..99;
        val MAJ = 0..9;
        val a = new Array[Array[Double](1)!](MIN, (Point) => new Array[Double](MAJ));

        for (val (i,j): Point(2) in ([MIN, MAJ] as Region))
            a(i)(j) = (i * j / PI);

        val d = a(MIN.max(0)/2);
        for (val (j): Point in MAJ) 
            chk(d(j) == (MIN.max(0)/2 * j / PI));

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new MultiDimensionalJavaArray().execute();
    }
}
