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
 * Test the shorthand syntax for array of arrays initializer.
 *
 * @author igor, 12/2005
 */

public class ArrayArrayInitializerShorthand extends x10Test {

    public def run(): boolean = {
        val r = Region.make(1..10, 1..10);
        val a = new Array[int](r, (Point)=>0n);
        val ia = new Array[Array[int]](r, ([i,j]: Point) => a);
        for (val [i,j]: Point(2) in ia) chk(ia(i, j) == a);
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayArrayInitializerShorthand().execute();
    }
}
