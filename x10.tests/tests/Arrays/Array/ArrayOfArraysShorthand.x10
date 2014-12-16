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
 * Test the syntax for creating an array of arrays.
 *
 * @author igor, 12/2005
 */

public class ArrayOfArraysShorthand extends x10Test {

    public def run(): boolean = {

        val r1 = Region.make(0,7);
        val r2 = Region.make(0,9);
        val r  = r1*r2;
        val ia = new Array[Array[Long](1)](r1, (Point)=> new Array[Long](r2, ([j]:Point)=>j));

        for (val [i,j]: Point in r) chk(ia(i)(j) == j);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayOfArraysShorthand().execute();
    }
}
