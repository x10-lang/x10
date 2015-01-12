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
 * Simple test for operator assignment of array elements.
 * Tests post and pre increment/decrement;
 */

public class ArrayOpAssign2 extends x10Test {

    var i: long = 1;
    var j: long = 1;

    public def run(): boolean = {

        val R = Region.make(1..10, 1..10);
        var ia: Array[long](2) = new Array[long](R, (Point)=>0);

        ia(i, j) = 1;

        chk(ia(i, j) == 1);
        chk((ia(i, j)++) == 1);
        chk(ia(i, j) == 2);
        chk((ia(i, j)--) == 2);
        chk(ia(i, j) == 1);
        chk((++ia(i, j)) == 2);
        chk(ia(i, j) == 2);
        chk((--ia(i, j)) == 1);
        chk(ia(i, j) == 1);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayOpAssign2().execute();
    }
}
