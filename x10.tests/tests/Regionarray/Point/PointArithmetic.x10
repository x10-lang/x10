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
 * Testing point arithmetic operations.
 *
 * @author igor, 2/2006
 */

public class PointArithmetic extends x10Test {

    public static DIM: long = 5;

    public def run(): boolean = {

        var sum: long = 0;
        val p = [2 as long, 2, 2, 2, 2] as Point(DIM);
        val q = [1 as long, 1, 1, 1, 1] as Point(DIM);
        var c: long = 2;

        // First test that the point/point arithmetic works

        var a: Point(DIM) = p + q;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += a(i);
        x10.io.Console.OUT.println("p+p: sum = "+sum);
        if (sum != 15) return false;

        var s: Point(DIM) = p - q;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += s(i);
        x10.io.Console.OUT.println("p-p: sum = "+sum);
        if (sum != 5) return false;

        var m: Point(DIM) = p * q;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += m(i);
        x10.io.Console.OUT.println("p*p: sum = "+sum);
        if (sum != 10) return false;

        var d: Point(DIM) = p / q;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += d(i);
        x10.io.Console.OUT.println("p/p: sum = "+sum);
        if (sum != 10) return false;

        // Unary +/-

        var u: Point(DIM) = -q;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += u(i);
        x10.io.Console.OUT.println("-p: sum = "+sum);
        if (sum != -5) return false;

        u = +q;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += u(i);
        x10.io.Console.OUT.println("+p: sum = "+sum);
        if (sum != 5) return false;

        // Then test that the point/constant arithmetic works

        a = p + c;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += a(i);
        x10.io.Console.OUT.println("p+c: sum = "+sum);
        if (sum != 20) return false;

        s = p - c;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += s(i);
        x10.io.Console.OUT.println("p-c: sum = "+sum);
        if (sum != 0) return false;

        m = p * c;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += m(i);
        x10.io.Console.OUT.println("p*c: sum = "+sum);
        if (sum != 20) return false;

        d = p / c;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += d(i);
        x10.io.Console.OUT.println("p/c: sum = "+sum);
        if (sum != 5) return false;

        a = c + p;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += a(i);
        x10.io.Console.OUT.println("c+p: sum = "+sum);
        if (sum != 20) return false;

        s = c - p;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += s(i);
        x10.io.Console.OUT.println("c-p: sum = "+sum);
        if (sum != 0) return false;

        m = c * p;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += m(i);
        x10.io.Console.OUT.println("c*p: sum = "+sum);
        if (sum != 20) return false;

        d = c / p;
        sum = 0;
        for (var i: long = 0; i < DIM; i++)
            sum += d(i);
        x10.io.Console.OUT.println("c/p: sum = "+sum);
        if (sum != 5) return false;

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new PointArithmetic().execute();
    }
}
