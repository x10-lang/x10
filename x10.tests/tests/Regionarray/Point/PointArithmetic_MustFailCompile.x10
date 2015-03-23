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

// If we do not run with STATIC_CHECKS it generates:
// Warning: Generated a dynamic check for the method call.
//OPTIONS: -STATIC_CHECKS

import harness.x10Test;
import x10.regionarray.*;

/**
 * Testing point arithmetic operations.
 *
 * @author igor, 2/2006
 */
public class PointArithmetic_MustFailCompile extends x10Test {

    public static DIM: long = 5;

    public def run(): boolean {

        var sum: long = 0;
        val p = [2 as long, 2, 2, 2, 2] as Point(DIM);
        val q = [1 as long, 1, 1, 1, 1] as Point(DIM);
        var c: long = 2;

        // Now test that the dimensionality is properly checked

        var gotException: boolean;
        var r: Point = [1, 2, 3, 4] as Point;

        var a: Point;
        var s: Point;
        var m: Point;
        var d: Point;
        
        a = p + r; // ERR
        s = p - r; // ERR
        m = p * r; // ERR
        d = p / r; // ERR
        a = r + p; // ERR
        s = r - p; // ERR
        m = r * p; // ERR
        d = r / p; // ERR
        
        return true;
    }
    def test() {
        val r = [1, 2, 3, 4] as Point;
        val p = [2 as long, 2, 2, 2] as Point;

        var a: Point;
        var s: Point;
        var m: Point;
        var d: Point;

        a = p + r;
        s = p - r;
        m = p * r;
        d = p / r;
        a = r + p;
        s = r - p;
        m = r * p;
        d = r / p;
    }
    def test2() {
        val r:Point = [1, 2, 3, 4] as Point;
        val p:Point = [2 as long, 2, 2, 2] as Point;

        var a: Point;
        var s: Point;
        var m: Point;
        var d: Point;

        a = p + r; // ERR
        s = p - r; // ERR
        m = p * r; // ERR
        d = p / r; // ERR
        a = r + p; // ERR
        s = r - p; // ERR
        m = r * p; // ERR
        d = r / p; // ERR
    }

    public static def main(var args: Rail[String]): void {
        new PointArithmetic_MustFailCompile().execute();
    }
}
