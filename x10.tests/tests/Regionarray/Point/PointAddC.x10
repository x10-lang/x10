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
 * Testing point arithmetic operations.
 *
 * @author igor, 2/2006
 */

public class PointAddC extends x10Test {

    public def run(): boolean {

        val p:Point = [2 as long, 2, 2, 2, 2] as Point;
        var c:long = 2;
        val a = p + c;

        var sum: long= 0;
        for (var i: long = 0; i < p.rank; i++)
            sum += a(i);

        if (sum != 20) return false;
        return true;
    }

    public static def main(var args: Rail[String]): void {
        new PointAddC().execute();
    }
}
