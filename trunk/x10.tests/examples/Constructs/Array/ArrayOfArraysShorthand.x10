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
import x10.array.Array;
import x10.array.Region;

/**
 * Test the syntax for creating an array of arrays.
 *
 * @author igor, 12/2005
 */

public class ArrayOfArraysShorthand extends x10Test {

    public def run(): boolean = {

        val r1: Region(1) = 0..7;
        val r2: Region(1) = 0..9;
        val r: Region(2) = Region.make([r1, r2]);
        val ia: Array[Array[Int]{rank==1}]{rank==1} = Array.make[Array[Int]{rank==1}](r1->here, (Point)=> Array.make[Int](r2->here, (Point)=>0));

        for (val (i,j): Point in r) chk(ia(i)(j) == 0);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayOfArraysShorthand().execute();
    }
}
