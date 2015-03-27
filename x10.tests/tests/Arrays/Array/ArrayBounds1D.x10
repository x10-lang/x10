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

import x10.util.Random;
import x10.regionarray.*;
import harness.x10Test;

/**
 * Array bounds test - 1D.
 *
 * Randomly generate 1D arrays and indices.
 *
 * See if the array index out of bounds exception occurs
 * in the right conditions.
 *
 * @author kemal 1/2005
 */

public class ArrayBounds1D extends x10Test {

    public def run(): boolean {

        val COUNT: int = 100n;
        val L: int = 10n;
        val K: int = 3n;

        for (var n: int = 0n; n < COUNT; n++) {
            var i: int = ranInt(-L-K, L+K);
            var lb1: int = ranInt(-L, L);
            var ub1: int = ranInt(lb1-1n, L); // include empty reg.
            var withinBounds: boolean = arrayAccess(lb1, ub1, i);
            chk(iff(withinBounds, i>=lb1 && i<=ub1));
        }
        return true;
    }

    /**
     * create a[lb1..ub1] then access a[i], return true iff
     * no array bounds exception occurred
     */
    private static def arrayAccess(var lb1: int, var ub1: int, var i: int): boolean {

        //pr(lb1+" "+ub1+" "+i);
        var a: Array[int](1) = new Array[int](Region.make(lb1,ub1), (Point)=>0n);
        var withinBounds: boolean = true;

        try {
            a(i) = 0xabcdef07L as Int;
            chk(a(i) ==  (0xabcdef07L as Int));
        } catch (var e: ArrayIndexOutOfBoundsException) {
            withinBounds = false;
        }
        //pr(lb1+" "+ub1+" "+i+" "+withinBounds);

        return withinBounds;
    }

    // utility methods after this point

    /**
     * print a string
     */
    private static def pr(var s: String): void {
        x10.io.Console.OUT.println(s);
    }

    /**
     * true iff (x if and only if y)
     */
    private static def iff(var x: boolean, var y: boolean): boolean {
        return x == y;
    }

    public static def main(var args: Rail[String]): void {
        new ArrayBounds1D().execute();
    }
}
