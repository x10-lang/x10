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
 * Simple array test #3. Tests declaration of arrays, storing in local
 * variables, accessing and updating for 1-d arrays.
 */

public class Array31 extends x10Test {

    public def run(): boolean = {
        val r:Region(1) = Region.make(1, 10);
        var ia: Array[Int](1) = new Array[Int](r, (Point)=>0n);
        ia(1) = 42n;
        return 42n == ia(1);
    }

    public static def main(var args: Rail[String]): void = {
        new Array31().execute();
    }
}
