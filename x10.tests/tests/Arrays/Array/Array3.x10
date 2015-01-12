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
 * Tests declaration of arrays, storing in local variables, accessing and
 * updating 2D arrays.
 */
public class Array3 extends x10Test {

    public def run(): boolean = {
    
        val r = Region.make(1..10, 1..10);
        val ia = new Array[int](r, (x:Point)=>0n);

        ia(1, 1) = 42n;

        return 42n == ia(1, 1);
    }

    public static def main(var args: Rail[String]): void = {
        new Array3().execute();
    }
}
