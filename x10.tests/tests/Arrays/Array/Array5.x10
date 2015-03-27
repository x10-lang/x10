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
 * Testing int[] method parameters and fields.
 */
public class Array5 extends x10Test {

    var ia: Array[int](1);

    public def this(): Array5 {}

    public def this(var ia: Array[int](1)): Array5 {
        this.ia = ia;
    }

    private def runtest(): boolean {
        ia(0) = 42n;
        return 42n == ia(0);
    }

    public def run(): boolean {
        val temp = new Array[int](1, (long)=>0n);
        temp(0) = 43n;
        return (new Array5(temp)).runtest();
    }

    public static def main(var args: Rail[String]): void {
        new Array5().execute();
    }
}
