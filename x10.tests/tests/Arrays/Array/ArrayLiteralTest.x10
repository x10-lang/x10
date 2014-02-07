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
 * Array Literal test.
 */

public class ArrayLiteralTest extends x10Test {

    public def run(): boolean = {

        val r = [1n, 2n, 3n];
        val a = [1 as Int,2 as Int,3 as Int];
        var sumr:int=0n;
        var suma:int=0n;
        for (i in a) suma += i;
        for (i in r) sumr += i;
        return suma==6n && sumr==6n;
    }

    public static def main(Rail[String]): void = {
        new ArrayLiteralTest().execute();
    }
}
