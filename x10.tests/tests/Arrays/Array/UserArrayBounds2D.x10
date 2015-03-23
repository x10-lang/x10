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
 * User defined type array bounds test - 2D.
 *
 * Randomly generate 2D arrays and indices.
 *
 * See if the array index out of bounds exception occurs
 * in the right conditions.
 *
 * @author kemal 11/2005
 */

public class UserArrayBounds2D extends x10Test {

    public def run(): boolean {

        val COUNT: int = 100n;
        val L: int = 10n;
        val K: int = 3n;

        for(var n: int = 0n; n < COUNT; n++) {
            var i: int = ranInt(-L-K, L+K);
            var j: int = ranInt(-L-K, L+K);
            var lb1: int = ranInt(-L, L);
            var lb2: int = ranInt(-L, L);
            var ub1: int = ranInt(lb1, L);
            var ub2: int = ranInt(lb2, L);
            var withinBounds: boolean = arrayAccess(lb1, ub1, lb2, ub2, i, j);
            chk(iff(withinBounds, i>=lb1 && i<=ub1 && j>=lb2 && j<=ub2));
        }
        return true;
    }

    /**
     * create a[lb1..ub1,lb2..ub2] then access a[i,j], return true iff
     * no array bounds exception occurred
     */
    private static def arrayAccess(lb1: int, ub1: int, lb2: int, ub2: int, i: int, j: int): boolean {
        var a: Array[Int](2) = new Array[Int](Region.make(lb1..ub1, lb2..ub2), ([i,j]: Point):Int=> 0n);

        var withinBounds: boolean = true;
        try {
            a(i, j) = 0xabcdef07L as Int;
            //pr("assigned");
            chk(a(i, j).equals(0xabcdef07L as Int));
        } catch (var e: ArrayIndexOutOfBoundsException) {
            withinBounds = false;
        }
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
    private static def iff(x: boolean, y: boolean) = x == y;
    public static def main(Rail[String]){
        new UserArrayBounds2D().execute();
    }
}
